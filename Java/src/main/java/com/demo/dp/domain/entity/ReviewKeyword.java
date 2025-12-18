package com.demo.dp.domain.entity;

/**
 * 点评关键词实体，对应表 review_keyword，用作轻量 RAG 的关键词索引。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class ReviewKeyword {
    private Long id; // 主键

    private Long reviewId; // 点评 ID

    private String keyword; // 关键词

    private Double weight; // 权重

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    // endregion
}

