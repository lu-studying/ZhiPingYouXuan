package com.demo.dp.domain.entity;

import java.time.LocalDateTime;

/**
 * 标签实体，对应表 tag。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class Tag {
    private Long id; // 主键

    private String name; // 标签名

    private String type; // 标签类型：user/shop/review（数据库 ENUM，MyBatis 映射为 String）

    private LocalDateTime createdAt; // 创建时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    // endregion
}

