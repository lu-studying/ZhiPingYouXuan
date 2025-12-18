package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 大模型调用日志实体，对应表 ai_call_log。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class AiCallLog {
    private Long id; // 主键

    private Long userId; // 触发用户 ID，可空

    private String type; // 调用类型：generate/recommend（数据库 ENUM，MyBatis 映射为 String）

    private String prompt; // 请求 Prompt

    private String responseRef; // 响应引用/存储位置

    private Integer latencyMs; // 耗时 ms

    private Integer status; // 状态：1 成功，0 失败

    private LocalDateTime createdAt; // 创建时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getResponseRef() { return responseRef; }
    public void setResponseRef(String responseRef) { this.responseRef = responseRef; }
    public Integer getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Integer latencyMs) { this.latencyMs = latencyMs; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    // endregion
}

