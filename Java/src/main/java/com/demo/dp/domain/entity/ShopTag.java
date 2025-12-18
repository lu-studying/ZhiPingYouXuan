package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 商家与标签关联实体，对应表 shop_tag。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class ShopTag {
    private Long id; // 主键

    private Long shopId; // 商家 ID

    private Long tagId; // 标签 ID

    private Double weight; // 权重

    private LocalDateTime updatedAt; // 更新时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // endregion
}

