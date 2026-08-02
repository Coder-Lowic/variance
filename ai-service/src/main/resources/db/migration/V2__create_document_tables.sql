-- V2: 创建文档缓存与用户偏好表
-- 创建时间: 2026/07/18

CREATE TABLE IF NOT EXISTS user_preference (
    user_id          VARCHAR(64)  NOT NULL PRIMARY KEY,
    preferences_json TEXT
);

CREATE TABLE IF NOT EXISTS document_cache (
    doc_id     VARCHAR(64)  NOT NULL PRIMARY KEY,
    file_name  VARCHAR(512) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_document_cache_created_at ON document_cache(created_at);
