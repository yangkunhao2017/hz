package com.yhwl.hz.common.security.component;

import com.yhwl.hz.common.core.util.SpringContextHolder;
import com.yhwl.hz.common.security.exception.UnauthorizedException;
import com.yhwl.hz.common.security.service.HzUser;
import com.yhwl.hz.common.security.service.HzUserDetailsService;
import com.yhwl.hz.common.security.util.HzSecurityMessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

/**
 * @author hz
 * @date 2020/9/29
 */
@Primary
@Service
@RequiredArgsConstructor
public class HzLocalResourceServerTokenServices implements ResourceServerTokenServices {

	private final TokenStore tokenStore;

	private final UserDetailsChecker preAuthenticationChecks;

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(accessToken);
		if (oAuth2Authentication == null) {
			return null;
		}

		OAuth2Request oAuth2Request = oAuth2Authentication.getOAuth2Request();
		if (!(oAuth2Authentication.getPrincipal() instanceof HzUser)) {
			return oAuth2Authentication;
		}

		String clientId = oAuth2Request.getClientId();

		Map<String, HzUserDetailsService> userDetailsServiceMap = SpringContextHolder
				.getBeansOfType(HzUserDetailsService.class);

		Optional<HzUserDetailsService> optional = userDetailsServiceMap.values().stream()
				.filter(service -> service.support(clientId, oAuth2Request.getGrantType()))
				.max(Comparator.comparingInt(Ordered::getOrder));

		if (!optional.isPresent()) {
			throw new InternalAuthenticationServiceException("UserDetailsService error , not register");
		}

		// ?????? username ?????? spring cache ???????????? ?????????
		HzUser pigUser = (HzUser) oAuth2Authentication.getPrincipal();

		UserDetails userDetails;
		try {
			userDetails = optional.get().loadUserByUser(pigUser);

			preAuthenticationChecks.check(userDetails);
		}
		catch (UsernameNotFoundException notFoundException) {
			throw new UnauthorizedException(String.format("%s username not found", pigUser.getUsername()),
					notFoundException);
		}
		catch (AccountStatusException accountStatusException) {
			throw new UnauthorizedException(
					HzSecurityMessageSourceUtil.getAccessor().getMessage(
							"AbstractUserDetailsAuthenticationProvider.locked", accountStatusException.getMessage()),
					accountStatusException);
		}
		Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userDetails, "N/A",
				userDetails.getAuthorities());
		OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
		authentication.setAuthenticated(true);
		return authentication;
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}

}
