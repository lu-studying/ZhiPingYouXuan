package com.demo.dp.dto;

/**
 * AI 点评推荐请求 DTO。
 *
 * <p>用于提交用户的偏好和推荐数量要求。
 */
public class AiRecommendRequest {

    /**
     * 用户偏好关键词，如“辣”“微辣”“不辣”“环境”“服务”。
     */
    private String preference;

    /**
     * 推荐条数上限，可选，默认 3。
     */
    private Integer limit;

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}

