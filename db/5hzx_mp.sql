USE hzx_mp;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wx_account
-- ----------------------------
DROP TABLE IF EXISTS `wx_account`;
CREATE TABLE `wx_account` (
                              `id` bigint NOT NULL COMMENT '主键',
                              `name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `account` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `appid` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `appsecret` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `url` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `token` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `aeskey` varchar(300) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `qr_url` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL,
                              `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `del_flag` char(1) CHARACTER SET utf8mb4 DEFAULT '0',
                              `tenant_id` bigint DEFAULT NULL COMMENT '租户Id',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='公众号账户表';

-- ----------------------------
-- Records of wx_account
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wx_account_fans
-- ----------------------------
DROP TABLE IF EXISTS `wx_account_fans`;
CREATE TABLE `wx_account_fans` (
                                   `id` bigint NOT NULL COMMENT '主键',
                                   `openid` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `subscribe_status` char(1) CHARACTER SET utf8mb4 DEFAULT '0',
                                   `subscribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
                                   `nickname` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `gender` varchar(10) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `language` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `country` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `province` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `city` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `tag_ids` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分组ID',
                                   `headimg_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `remark` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `wx_account_id` bigint DEFAULT NULL COMMENT '微信公众号ID',
                                   `wx_account_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `wx_account_appid` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `del_flag` char(1) CHARACTER SET utf8mb4 DEFAULT '0',
                                   `tenant_id` bigint DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_1` (`openid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信公众号粉丝表';

-- ----------------------------
-- Records of wx_account_fans
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wx_account_tag
-- ----------------------------
DROP TABLE IF EXISTS `wx_account_tag`;
CREATE TABLE `wx_account_tag` (
                                  `id` bigint NOT NULL COMMENT '主键',
                                  `tag` varchar(100) CHARACTER SET utf8mb4 NOT NULL COMMENT '标签',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '创建人',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '修改人',
                                  `create_time` datetime NOT NULL COMMENT '创建时间',
                                  `update_time` datetime NOT NULL COMMENT '修改时间',
                                  `del_flag` char(1) CHARACTER SET utf8mb4 NOT NULL COMMENT '删除标记',
                                  `tenant_id` bigint NOT NULL COMMENT '租户ID',
                                  `wx_account_id` bigint NOT NULL COMMENT '微信账号ID',
                                  `wx_account_name` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '微信账号名称',
                                  `wx_account_appid` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT 'appID',
                                  `tag_id` bigint NOT NULL COMMENT '标签ID',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of wx_account_tag
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wx_auto_reply
-- ----------------------------
DROP TABLE IF EXISTS `wx_auto_reply`;
CREATE TABLE `wx_auto_reply` (
                                 `id` bigint NOT NULL COMMENT '主键',
                                 `type` char(2) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '类型（1、关注时回复；2、消息回复；3、关键词回复）',
                                 `req_key` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '关键词',
                                 `req_type` char(10) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '请求消息类型（text：文本；image：图片；voice：语音；video：视频；shortvideo：小视频；location：地理位置）',
                                 `rep_type` char(10) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复消息类型（text：文本；image：图片；voice：语音；video：视频；music：音乐；news：图文）',
                                 `rep_mate` char(10) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复类型文本匹配类型（1、全匹配，2、半匹配）',
                                 `rep_content` text CHARACTER SET utf8mb4 COMMENT '回复类型文本保存文字',
                                 `rep_media_id` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复类型imge、voice、news、video的mediaID或音乐缩略图的媒体id',
                                 `rep_name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复的素材名、视频和音乐的标题',
                                 `rep_desc` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '视频和音乐的描述',
                                 `rep_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '链接',
                                 `rep_hq_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '高质量链接',
                                 `rep_thumb_media_id` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '缩略图的媒体id',
                                 `rep_thumb_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '缩略图url',
                                 `content` json DEFAULT NULL COMMENT '图文消息的内容',
                                 `app_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '公众号ID',
                                 `remark` varchar(100) CHARACTER SET utf8  DEFAULT NULL COMMENT '备注',
                                 `del_flag` char(1) CHARACTER SET utf8  DEFAULT '0' COMMENT '逻辑删除标记（0：显示；1：隐藏）',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信自动回复';

-- ----------------------------
-- Records of wx_auto_reply
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wx_mp_menu
-- ----------------------------
DROP TABLE IF EXISTS `wx_mp_menu`;
CREATE TABLE `wx_mp_menu` (
                              `id` bigint NOT NULL COMMENT '主键',
                              `menu` json DEFAULT NULL COMMENT '菜单',
                              `wx_account_id` bigint DEFAULT NULL COMMENT '公众号ID',
                              `wx_account_appid` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '公众号APPID',
                              `wx_account_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '公众号名称',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `del_flag` char(1) CHARACTER SET utf8mb4 DEFAULT '0' COMMENT '删除标记',
                              `pub_flag` char(1) CHARACTER SET utf8mb4 DEFAULT '0' COMMENT '发布标志',
                              `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信菜单表';

-- ----------------------------
-- Records of wx_mp_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for wx_msg
-- ----------------------------
DROP TABLE IF EXISTS `wx_msg`;
CREATE TABLE `wx_msg` (
                          `id` bigint NOT NULL COMMENT '主键',
                          `app_name` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '公众号名称',
                          `app_logo` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '公众号logo',
                          `wx_user_id` varchar(32) CHARACTER SET utf8  NOT NULL COMMENT '微信用户ID',
                          `nick_name` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '微信用户昵称',
                          `headimg_url` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '微信用户头像',
                          `type` char(2) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '消息分类（1、用户发给公众号；2、公众号发给用户；）',
                          `rep_type` char(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '消息类型（text：文本；image：图片；voice：语音；video：视频；shortvideo：小视频；location：地理位置；music：音乐；news：图文；event：推送事件）',
                          `rep_event` char(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '事件类型（subscribe：关注；unsubscribe：取关；CLICK、VIEW：菜单事件）',
                          `rep_content` text CHARACTER SET utf8mb4 COMMENT '回复类型文本保存文字、地理位置信息',
                          `rep_media_id` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复类型imge、voice、news、video的mediaID或音乐缩略图的媒体id',
                          `rep_name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复的素材名、视频和音乐的标题',
                          `rep_desc` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '视频和音乐的描述',
                          `rep_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '链接',
                          `rep_hq_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '高质量链接',
                          `content` json DEFAULT NULL COMMENT '图文消息的内容',
                          `rep_thumb_media_id` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '缩略图的媒体id',
                          `rep_thumb_url` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '缩略图url',
                          `rep_location_x` double DEFAULT NULL COMMENT '地理位置维度',
                          `rep_location_y` double DEFAULT NULL COMMENT '地理位置经度',
                          `rep_scale` double DEFAULT NULL COMMENT '地图缩放大小',
                          `read_flag` char(2) CHARACTER SET utf8  DEFAULT '1' COMMENT '已读标记（1：是；0：否）',
                          `app_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '公众号ID',
                          `open_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '微信唯一标识',
                          `remark` varchar(100) CHARACTER SET utf8  DEFAULT NULL COMMENT '备注',
                          `del_flag` char(1) CHARACTER SET utf8  DEFAULT '0' COMMENT '逻辑删除标记（0：显示；1：隐藏）',
                          `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='微信消息';

-- ----------------------------
-- Records of wx_msg
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
