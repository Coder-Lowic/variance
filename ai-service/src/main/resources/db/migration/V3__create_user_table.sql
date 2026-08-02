-- V3: 创建用户表（JWT认证）
-- 创建时间: 2026/07/18

CREATE TABLE IF NOT EXISTS app_user (
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL UNIQUE,
    password    VARCHAR(256) NOT NULL,
    email       VARCHAR(128),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_app_user_username ON app_user(username);
