USE hzx_job;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
                                      `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                      `trigger_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                      `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                      `blob_data` blob,
                                      PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
                                      CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
                                  `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                  `calendar_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                  `calendar` blob NOT NULL,
                                  PRIMARY KEY (`sched_name`,`calendar_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
                                      `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                      `trigger_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                      `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                      `cron_expression` varchar(200) CHARACTER SET utf8  NOT NULL,
                                      `time_zone_id` varchar(80) CHARACTER SET utf8  DEFAULT NULL,
                                      PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
                                      CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
                                       `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                       `entry_id` varchar(95) CHARACTER SET utf8  NOT NULL,
                                       `trigger_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                       `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                       `instance_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                       `fired_time` bigint NOT NULL,
                                       `sched_time` bigint NOT NULL,
                                       `priority` int NOT NULL,
                                       `state` varchar(16) CHARACTER SET utf8  NOT NULL,
                                       `job_name` varchar(200) CHARACTER SET utf8  DEFAULT NULL,
                                       `job_group` varchar(200) CHARACTER SET utf8  DEFAULT NULL,
                                       `is_nonconcurrent` varchar(1) CHARACTER SET utf8  DEFAULT NULL,
                                       `requests_recovery` varchar(1) CHARACTER SET utf8  DEFAULT NULL,
                                       PRIMARY KEY (`sched_name`,`entry_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
                                    `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                    `job_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                    `job_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                    `description` varchar(250) CHARACTER SET utf8  DEFAULT NULL,
                                    `job_class_name` varchar(250) CHARACTER SET utf8  NOT NULL,
                                    `is_durable` varchar(1) CHARACTER SET utf8  NOT NULL,
                                    `is_nonconcurrent` varchar(1) CHARACTER SET utf8  NOT NULL,
                                    `is_update_data` varchar(1) CHARACTER SET utf8  NOT NULL,
                                    `requests_recovery` varchar(1) CHARACTER SET utf8  NOT NULL,
                                    `job_data` blob,
                                    PRIMARY KEY (`sched_name`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
                              `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                              `lock_name` varchar(40) CHARACTER SET utf8  NOT NULL,
                              PRIMARY KEY (`sched_name`,`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
                                            `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                            `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                            PRIMARY KEY (`sched_name`,`trigger_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
                                        `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                        `instance_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                        `last_checkin_time` bigint NOT NULL,
                                        `checkin_interval` bigint NOT NULL,
                                        PRIMARY KEY (`sched_name`,`instance_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
                                        `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                        `trigger_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                        `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                        `repeat_count` bigint NOT NULL,
                                        `repeat_interval` bigint NOT NULL,
                                        `times_triggered` bigint NOT NULL,
                                        PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
                                        CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_IBFK_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
                                         `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                         `trigger_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                         `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                         `str_prop_1` varchar(512) CHARACTER SET utf8  DEFAULT NULL,
                                         `str_prop_2` varchar(512) CHARACTER SET utf8  DEFAULT NULL,
                                         `str_prop_3` varchar(512) CHARACTER SET utf8  DEFAULT NULL,
                                         `int_prop_1` int DEFAULT NULL,
                                         `int_prop_2` int DEFAULT NULL,
                                         `long_prop_1` bigint DEFAULT NULL,
                                         `long_prop_2` bigint DEFAULT NULL,
                                         `dec_prop_1` decimal(13,4) DEFAULT NULL,
                                         `dec_prop_2` decimal(13,4) DEFAULT NULL,
                                         `bool_prop_1` varchar(1) CHARACTER SET utf8  DEFAULT NULL,
                                         `bool_prop_2` varchar(1) CHARACTER SET utf8  DEFAULT NULL,
                                         PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
                                         CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_IBFK_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
                                 `sched_name` varchar(120) CHARACTER SET utf8  NOT NULL,
                                 `trigger_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                 `trigger_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                 `job_name` varchar(200) CHARACTER SET utf8  NOT NULL,
                                 `job_group` varchar(200) CHARACTER SET utf8  NOT NULL,
                                 `description` varchar(250) CHARACTER SET utf8  DEFAULT NULL,
                                 `next_fire_time` bigint DEFAULT NULL,
                                 `prev_fire_time` bigint DEFAULT NULL,
                                 `priority` int DEFAULT NULL,
                                 `trigger_state` varchar(16) CHARACTER SET utf8  NOT NULL,
                                 `trigger_type` varchar(8) CHARACTER SET utf8  NOT NULL,
                                 `start_time` bigint NOT NULL,
                                 `end_time` bigint DEFAULT NULL,
                                 `calendar_name` varchar(200) CHARACTER SET utf8  DEFAULT NULL,
                                 `misfire_instr` smallint DEFAULT NULL,
                                 `job_data` blob,
                                 PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`) USING BTREE,
                                 KEY `sched_name` (`sched_name`,`job_name`,`job_group`) USING BTREE,
                                 CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
                           `job_id` bigint NOT NULL COMMENT '??????id',
                           `job_name` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '????????????',
                           `job_group` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '????????????',
                           `job_order` char(1) CHARACTER SET utf8mb4 DEFAULT '1' COMMENT '???????????????????????????????????????????????????????????????9????????????1',
                           `job_type` char(1) CHARACTER SET utf8mb4 NOT NULL DEFAULT '1' COMMENT '1???java???;2???spring bean??????;3???rest??????;4???jar??????;9??????',
                           `execute_path` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'job_type=3??????rest????????????????????????rest get??????,????????????String????????????0?????????1??????;job_type=4??????jar??????;???????????????',
                           `class_name` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'job_type=1?????????????????????;job_type=2??????spring bean??????;???????????????',
                           `method_name` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '????????????',
                           `method_params_value` varchar(2000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????',
                           `cron_expression` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'cron???????????????',
                           `misfire_policy` varchar(20) CHARACTER SET utf8mb4 DEFAULT '3' COMMENT '?????????????????????1???????????????????????? 2???????????????????????? 3??????????????????',
                           `job_tenant_type` char(1) CHARACTER SET utf8mb4 DEFAULT '1' COMMENT '1??????????????????;2?????????????????????',
                           `job_status` char(1) CHARACTER SET utf8mb4 DEFAULT '0' COMMENT '?????????1????????????;2????????????;3?????????;4?????????;???',
                           `job_execute_status` char(1) CHARACTER SET utf8mb4 DEFAULT '0' COMMENT '?????????0?????? 1?????????',
                           `create_by` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????',
                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????????????',
                           `update_by` varchar(64) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '?????????',
                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????????????',
                           `start_time` timestamp NULL DEFAULT NULL COMMENT '??????????????????',
                           `previous_time` timestamp NULL DEFAULT NULL COMMENT '??????????????????',
                           `next_time` timestamp NULL DEFAULT NULL COMMENT '??????????????????',
                           `tenant_id` bigint DEFAULT '1' COMMENT '??????',
                           `remark` varchar(500) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '????????????',
                           PRIMARY KEY (`job_id`,`job_name`,`job_group`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='?????????????????????';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
                               `job_log_id` bigint NOT NULL COMMENT '????????????ID',
                               `job_id` bigint NOT NULL COMMENT '??????id',
                               `job_name` varchar(64) CHARACTER SET utf8  DEFAULT NULL COMMENT '????????????',
                               `job_group` varchar(64) CHARACTER SET utf8  DEFAULT NULL COMMENT '????????????',
                               `job_order` char(1) CHARACTER SET utf8  DEFAULT NULL COMMENT '???????????????????????????????????????????????????????????????9????????????1',
                               `job_type` char(1) CHARACTER SET utf8  NOT NULL DEFAULT '1' COMMENT '1???java???;2???spring bean??????;3???rest??????;4???jar??????;9??????',
                               `execute_path` varchar(500) CHARACTER SET utf8  DEFAULT NULL COMMENT 'job_type=3??????rest????????????????????????post??????;job_type=4??????jar??????;???????????????',
                               `class_name` varchar(500) CHARACTER SET utf8  DEFAULT NULL COMMENT 'job_type=1?????????????????????;job_type=2??????spring bean??????;???????????????',
                               `method_name` varchar(500) CHARACTER SET utf8  DEFAULT NULL COMMENT '????????????',
                               `method_params_value` varchar(2000) CHARACTER SET utf8  DEFAULT NULL COMMENT '?????????',
                               `cron_expression` varchar(255) CHARACTER SET utf8  DEFAULT NULL COMMENT 'cron???????????????',
                               `job_message` varchar(500) CHARACTER SET utf8  DEFAULT NULL COMMENT '????????????',
                               `job_log_status` char(1) CHARACTER SET utf8  DEFAULT '0' COMMENT '???????????????0?????? 1?????????',
                               `execute_time` varchar(30) CHARACTER SET utf8  DEFAULT NULL COMMENT '????????????',
                               `exception_info` varchar(2000) CHARACTER SET utf8  DEFAULT '' COMMENT '????????????',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '????????????',
                               `tenant_id` bigint NOT NULL DEFAULT '1' COMMENT '??????id',
                               PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='???????????????????????????';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_group
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE `xxl_job_group` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `app_name` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '?????????AppName',
                                 `title` varchar(12) CHARACTER SET utf8mb4 NOT NULL COMMENT '???????????????',
                                 `address_type` tinyint NOT NULL DEFAULT '0' COMMENT '????????????????????????0=???????????????1=????????????',
                                 `address_list` text CHARACTER SET utf8mb4 COMMENT '?????????????????????????????????????????????',
                                 `update_time` datetime DEFAULT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_group
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `job_group` int NOT NULL COMMENT '???????????????ID',
                                `job_desc` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
                                `add_time` datetime DEFAULT NULL,
                                `update_time` datetime DEFAULT NULL,
                                `author` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '??????',
                                `alarm_email` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '????????????',
                                `schedule_type` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT 'NONE' COMMENT '????????????',
                                `schedule_conf` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????????????????????????????????????????',
                                `misfire_strategy` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT 'DO_NOTHING' COMMENT '??????????????????',
                                `executor_route_strategy` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????????????????',
                                `executor_handler` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '???????????????handler',
                                `executor_param` varchar(512) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????????????????',
                                `executor_block_strategy` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '??????????????????',
                                `executor_timeout` int NOT NULL DEFAULT '0' COMMENT '????????????????????????????????????',
                                `executor_fail_retry_count` int NOT NULL DEFAULT '0' COMMENT '??????????????????',
                                `glue_type` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT 'GLUE??????',
                                `glue_source` mediumtext CHARACTER SET utf8mb4 COMMENT 'GLUE?????????',
                                `glue_remark` varchar(128) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'GLUE??????',
                                `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE????????????',
                                `child_jobid` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????ID?????????????????????',
                                `trigger_status` tinyint NOT NULL DEFAULT '0' COMMENT '???????????????0-?????????1-??????',
                                `trigger_last_time` bigint NOT NULL DEFAULT '0' COMMENT '??????????????????',
                                `trigger_next_time` bigint NOT NULL DEFAULT '0' COMMENT '??????????????????',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_info
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_lock
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_lock`;
CREATE TABLE `xxl_job_lock` (
                                `lock_name` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '?????????',
                                PRIMARY KEY (`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_lock
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_log
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE `xxl_job_log` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `job_group` int NOT NULL COMMENT '???????????????ID',
                               `job_id` int NOT NULL COMMENT '???????????????ID',
                               `executor_address` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '???????????????????????????????????????',
                               `executor_handler` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '???????????????handler',
                               `executor_param` varchar(512) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '?????????????????????',
                               `executor_sharding_param` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '??????????????????????????????????????? 1/2',
                               `executor_fail_retry_count` int NOT NULL DEFAULT '0' COMMENT '??????????????????',
                               `trigger_time` datetime DEFAULT NULL COMMENT '??????-??????',
                               `trigger_code` int NOT NULL COMMENT '??????-??????',
                               `trigger_msg` text CHARACTER SET utf8mb4 COMMENT '??????-??????',
                               `handle_time` datetime DEFAULT NULL COMMENT '??????-??????',
                               `handle_code` int NOT NULL COMMENT '??????-??????',
                               `handle_msg` text CHARACTER SET utf8mb4 COMMENT '??????-??????',
                               `alarm_status` tinyint NOT NULL DEFAULT '0' COMMENT '???????????????0-?????????1-???????????????2-???????????????3-????????????',
                               PRIMARY KEY (`id`) USING BTREE,
                               KEY `I_trigger_time` (`trigger_time`) USING BTREE,
                               KEY `I_handle_code` (`handle_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_log_report
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log_report`;
CREATE TABLE `xxl_job_log_report` (
                                      `id` int NOT NULL AUTO_INCREMENT,
                                      `trigger_day` datetime DEFAULT NULL COMMENT '??????-??????',
                                      `running_count` int NOT NULL DEFAULT '0' COMMENT '?????????-????????????',
                                      `suc_count` int NOT NULL DEFAULT '0' COMMENT '????????????-????????????',
                                      `fail_count` int NOT NULL DEFAULT '0' COMMENT '????????????-????????????',
                                      `update_time` datetime DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_log_report
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_logglue
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_logglue`;
CREATE TABLE `xxl_job_logglue` (
                                   `id` int NOT NULL AUTO_INCREMENT,
                                   `job_id` int NOT NULL COMMENT '???????????????ID',
                                   `glue_type` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'GLUE??????',
                                   `glue_source` mediumtext CHARACTER SET utf8mb4 COMMENT 'GLUE?????????',
                                   `glue_remark` varchar(128) CHARACTER SET utf8mb4 NOT NULL COMMENT 'GLUE??????',
                                   `add_time` datetime DEFAULT NULL,
                                   `update_time` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_logglue
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_registry
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_registry`;
CREATE TABLE `xxl_job_registry` (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `registry_group` varchar(50) CHARACTER SET utf8mb4 NOT NULL,
                                    `registry_key` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
                                    `registry_value` varchar(255) CHARACTER SET utf8mb4 NOT NULL,
                                    `update_time` datetime DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE,
                                    KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_registry
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `username` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '??????',
                                `password` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '??????',
                                `role` tinyint NOT NULL COMMENT '?????????0-???????????????1-?????????',
                                `permission` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '??????????????????ID???????????????????????????',
                                PRIMARY KEY (`id`) USING BTREE,
                                UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of xxl_job_user
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
