-- V1: 创建对话核心表
-- 创建时间: 2026/07/18

CREATE TABLE IF NOT EXISTS chat_session (
    session_id  VARCHAR(64)  NOT NULL PRIMARY KEY,
    user_id     VARCHAR(64)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chat_session_user_id ON chat_session(user_id);

CREATE TABLE IF NOT EXISTS chat_message (
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    role        VARCHAR(32)  NOT NULL,
    content     TEXT,
    timestamp   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    session_id  VARCHAR(64)  NOT NULL REFERENCES chat_session(session_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chat_message_session_id ON chat_message(session_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_timestamp  ON chat_message(timestamp);
