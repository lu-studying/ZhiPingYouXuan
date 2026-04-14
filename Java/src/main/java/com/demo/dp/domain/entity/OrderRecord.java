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

    private String orderNo; // 订单号

    private BigDecimal amount; // 消费金额，保留两位小数

    private Integer payStatus; // 支付状态：0待支付，1已支付，2已取消

    private String payMethod; // 支付方式（模拟）

    private String payTxnNo; // 模拟交易流水号

    private LocalDateTime paidAt; // 支付时间

    private LocalDateTime visitTime; // 到店时间

    private String items; // 消费项明细 JSON

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 更新时间

    // region getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getPayStatus() { return payStatus; }
    public void setPayStatus(Integer payStatus) { this.payStatus = payStatus; }
    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    public String getPayTxnNo() { return payTxnNo; }
    public void setPayTxnNo(String payTxnNo) { this.payTxnNo = payTxnNo; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getVisitTime() { return visitTime; }
    public void setVisitTime(LocalDateTime visitTime) { this.visitTime = visitTime; }
    public String getItems() { return items; }
    public void setItems(String items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // endregion
}

