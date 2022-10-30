FROM pig4cloud/java:8-jre

MAINTAINER wangiegie@gmail.com

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms128m -Xmx256m -Djava.security.egd=file:/dev/./urandom"

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir -p /hz-mp-platform

WORKDIR /hz-mp-platform

EXPOSE 6000

ADD ./target/hz-mp-platform.jar ./

CMD sleep 180;java $JAVA_OPTS -jar hz-mp-platform.jar
