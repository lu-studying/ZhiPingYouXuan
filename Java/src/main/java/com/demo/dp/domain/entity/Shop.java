package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 商家实体，对应表 shop。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class Shop {
    private Long id; // 主键

    private String name; // 商家名称

    private String category; // 品类

    private String address; // 地址

    private Double lng; // 经度

    private Double lat; // 纬度

    private Double avgPrice; // 人均价格

    private Double avgScore; // 平均评分

    private Integer status; // 状态：1 正常，0 下线

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getAvgPrice() { return avgPrice; }
    public void setAvgPrice(Double avgPrice) { this.avgPrice = avgPrice; }
    public Double getAvgScore() { return avgScore; }
    public void setAvgScore(Double avgScore) { this.avgScore = avgScore; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // endregion
}

