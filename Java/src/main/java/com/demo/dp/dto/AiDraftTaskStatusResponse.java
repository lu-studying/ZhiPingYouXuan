package com.demo.dp.dto;

/**
 * 异步 AI 草稿任务状态响应。
 */
public class AiDraftTaskStatusResponse {
    /**
     * queued / running / succeeded / failed
     */
    private String status;
    private String draft;
    private String error;

    public AiDraftTaskStatusResponse() {}

    public AiDraftTaskStatusResponse(String status, String draft, String error) {
        this.status = status;
        this.draft = draft;
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

