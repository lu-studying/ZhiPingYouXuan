package com.demo.dp.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜单实体，对应表 menu。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class Menu {
    private Long id; // 主键

    private Long shopId; // 商家ID

    private String name; // 菜品名称

    private String description; // 菜品描述

    private BigDecimal price; // 价格

    private String image; // 图片URL

    private Integer isRecommended; // 是否推荐：1是，0否

    private Integer sortOrder; // 排序顺序（数字越小越靠前）

    private Integer status; // 状态：1正常，0下线

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public Integer getIsRecommended() { return isRecommended; }
    public void setIsRecommended(Integer isRecommended) { this.isRecommended = isRecommended; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // endregion
}

