SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_credentials
-- ----------------------------
DROP TABLE IF EXISTS `api_credentials`;
CREATE TABLE `api_credentials`
(
    `id`             bigint                                                        NOT NULL AUTO_INCREMENT,
    `owner_id`       bigint                                                        NOT NULL COMMENT '第三方用户或应用 ID',
    `api_key_hash`   char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NOT NULL COMMENT 'SHA-256(apiKey)',
    `api_secret_enc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NOT NULL COMMENT '加密后的 apiSecret',
    `status`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT 'ACTIVE DISABLED REVOKED',
    `scope`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限范围',
    `plan`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '套餐类型',
    `expires_at`     datetime                                                      NULL DEFAULT NULL COMMENT '过期时间',
    `last_used_at`   datetime                                                      NULL DEFAULT NULL COMMENT '最后一次调用',
    `created_at`     datetime                                                      NOT NULL,
    `updated_at`     datetime                                                      NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_api_key_hash` (`api_key_hash` ASC) USING BTREE,
    INDEX `idx_owner_id` (`owner_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '接口凭证'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of api_credentials
-- ----------------------------
INSERT INTO `api_credentials`
VALUES (1, 0, 'zQ1ouKPSNjxvOY+iPJiIeCiKZVAohvMdqHL82ORxuu8=',
        'uZPsNn6SG64xdZx1RpnKybIBO7qlkdbrPCONYDHNy68eED1AJpZHLV/L0IdQHo8m', '1', NULL, NULL, '2028-04-20 13:44:02',
        '2026-01-20 13:44:05', '2026-01-20 13:44:08', '2026-01-20 13:44:11');

-- ----------------------------
-- Table structure for file_metadata
-- ----------------------------
DROP TABLE IF EXISTS `file_metadata`;
CREATE TABLE `file_metadata`
(
    `id`           bigint                                                        NOT NULL COMMENT '主键',
    `file_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
    `object_name`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'MinIO 对象名',
    `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT 'MIME 类型',
    `size`         bigint                                                        NULL     DEFAULT 0 COMMENT '文件大小',
    `access_level` tinyint                                                       NOT NULL DEFAULT 1 COMMENT '访问级别：0=公开，1=需登录',
    `biz_type`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '业务类型',
    `biz_id`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '业务实体 ID',
    `status`       tinyint                                                       NOT NULL DEFAULT 1 COMMENT '文件状态 0=删除,1=存在',
    `create_time`  datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `delete_time`  datetime                                                      NULL     DEFAULT NULL COMMENT '删除日期',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_object_name` (`object_name` ASC) USING BTREE,
    INDEX `idx_biz` (`biz_type` ASC, `biz_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_metadata
-- ----------------------------

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`
(
    `id`              bigint                                                           NOT NULL AUTO_INCREMENT COMMENT '日志id',
    `trace_id`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '追踪id',
    `uri`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '接口uri',
    `http_method`     varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT 'http请求方式',
    `query_params`    text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '查询参数',
    `body_params`     text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '请求体参数',
    `response_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NULL COMMENT '响应结果',
    `request_time`    datetime                                                      NOT NULL COMMENT '请求时间戳',
    `request_ip`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '请求ip',
    `user_id`         bigint                                                           NULL DEFAULT NULL COMMENT '用户id',
    `cost`            bigint                                                        NULL DEFAULT NULL COMMENT '请求耗时',
    `exception`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '异常信息',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `trace_id` (`trace_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 79
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统接口日志'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`            bigint                                                           NOT NULL AUTO_INCREMENT COMMENT '管理员id',
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
VALUES (1, '管理员', 'admin', '$2a$10$tinSf4yBSjVWnI1qwxLt6uwEuWgqMyjDLmBRP3MZJ9/q/O7CsgSTC', '2025-12-18 14:13:50',
        '127.0.0.1', NULL, 1, NULL, NULL, NULL, NULL, '2025-12-09 16:19:11');

SET FOREIGN_KEY_CHECKS = 1;
