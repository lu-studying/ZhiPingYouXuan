package com.demo.dp.dto;

/**
 * AI 点评草稿生成请求 DTO。
 *
 * <p>作用：向后端提交用户偏好和生成参数，便于调用大模型生成点评草稿。
 */
public class AiDraftRequest {

    /**
     * 口味偏好/生成提示，如："偏辣"、"不要太咸"、"喜欢环境描述"。
     */
    private String preference;

    /**
     * 期望的字数上限（可选）。如果不传，默认使用 Prompt 中的建议字数。
     */
    private Integer maxTokens;

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
}

