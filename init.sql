SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`
(
    `id`              int                                                           NOT NULL AUTO_INCREMENT COMMENT '日志id',
    `trace_id`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '追踪id',
    `uri`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口uri',
    `http_method`     varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT 'http请求方式',
    `query_params`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '查询参数',
    `body_params`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '请求体参数',
    `response_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NULL COMMENT '响应结果',
    `request_time`    datetime                                                      NOT NULL COMMENT '请求时间戳',
    `request_ip`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '请求ip',
    `user_id`         int                                                           NULL DEFAULT NULL COMMENT '用户id',
    `cost`            bigint                                                        NULL DEFAULT NULL COMMENT '请求耗时',
    `exception`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '异常信息',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `trace_id` (`trace_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统日志'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`            int                                                           NOT NULL AUTO_INCREMENT COMMENT '管理员id',
    `nickname`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '昵称',
    `username`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '用户名',
    `password`      varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码(加密)',
    `last_login`    datetime                                                      NULL     DEFAULT NULL COMMENT '上一次登录时间',
    `last_login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '上一次登录IP',
    `last_update`   datetime                                                      NULL     DEFAULT NULL COMMENT '最后更新日期',
    `status`        tinyint(1)                                                    NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
    `introduction`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '简介',
    `avatar`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '头像',
    `phone`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '手机号',
    `email`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '电子邮箱',
    `register_date` datetime                                                      NULL     DEFAULT NULL COMMENT '注册日期',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, '管理员', 'admin', '$2a$10$tinSf4yBSjVWnI1qwxLt6uwEuWgqMyjDLmBRP3MZJ9/q/O7CsgSTC', '2025-12-11 17:34:48',
        '127.0.0.1', NULL, 1, NULL, NULL, NULL, NULL, '2025-12-09 16:19:11');

SET FOREIGN_KEY_CHECKS = 1;
