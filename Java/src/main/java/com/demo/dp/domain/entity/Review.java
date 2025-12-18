package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 点评实体，对应表 review。
 * 说明：images 以 JSON 字符串存储，应用层自行解析。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class Review {
    private Long id; // 主键

    private Long shopId; // 商家 ID

    private Long userId; // 用户 ID

    private Integer rating; // 评分 1-5

    private String content; // 点评正文

    private String images; // 图片列表 JSON

    private Boolean isAiGenerated; // 是否 AI 生成

    private Integer likeCount; // 点赞数

    private Integer status; // 状态：1 正常，0 屏蔽/审核中

    private LocalDateTime createdAt; // 创建时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Boolean getIsAiGenerated() { return isAiGenerated; }
    public void setIsAiGenerated(Boolean aiGenerated) { this.isAiGenerated = aiGenerated; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    // endregion
}

