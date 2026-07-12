package com.lowic.ai.exception;

/**
 * 内容审核失败异常
 */
public class ModerationException extends RuntimeException {

    private final String contentId;

    public ModerationException(String message) {
        super(message);
        this.contentId = null;
    }

    public ModerationException(String message, String contentId) {
        super(message);
        this.contentId = contentId;
    }

    public ModerationException(String message, Throwable cause) {
        super(message, cause);
        this.contentId = null;
    }

    public String getContentId() {
        return contentId;
    }
}
