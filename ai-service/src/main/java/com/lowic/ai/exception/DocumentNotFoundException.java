package com.lowic.ai.exception;

/**
 * 文档不存在或已过期异常
 */
public class DocumentNotFoundException extends RuntimeException {

    private final String docId;

    public DocumentNotFoundException(String docId) {
        super("文档不存在或已过期，请重新上传: " + docId);
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }
}
