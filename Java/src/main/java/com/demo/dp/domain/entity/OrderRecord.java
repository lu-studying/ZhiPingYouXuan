package com.demo.dp.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费记录实体，对应表 order_record。
 * MyBatis 使用，字段命名与数据库表对应（下划线转驼峰由 MyBatis 配置处理）。
 */
public class OrderRecord {
    private Long id; // 主键

    private Long shopId; // 商家 ID

    private Long userId; // 用户 ID

    private BigDecimal amount; // 消费金额，保留两位小数

    private LocalDateTime visitTime; // 到店时间

    private String items; // 消费项明细 JSON

    private LocalDateTime createdAt; // 创建时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getVisitTime() { return visitTime; }
    public void setVisitTime(LocalDateTime visitTime) { this.visitTime = visitTime; }
    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    // endregion
}

