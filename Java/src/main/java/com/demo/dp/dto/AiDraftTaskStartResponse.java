package com.demo.dp.dto;

/**
 * 异步 AI 草稿任务启动响应。
 */
public class AiDraftTaskStartResponse {
    private String taskId;

    public AiDraftTaskStartResponse() {}

    public AiDraftTaskStartResponse(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

