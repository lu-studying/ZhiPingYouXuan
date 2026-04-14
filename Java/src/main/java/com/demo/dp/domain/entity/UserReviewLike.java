package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 用户点赞点评关系实体，对应 user_review_like 表。
 */
public class UserReviewLike {
    private Long id;
    private Long userId;
    private Long reviewId;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
