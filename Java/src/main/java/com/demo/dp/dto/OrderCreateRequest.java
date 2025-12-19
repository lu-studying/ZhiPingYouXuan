package com.demo.dp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建消费记录请求 DTO。
 */
public class OrderCreateRequest {

    /**
     * 消费金额，单位：元。
     */
    private BigDecimal amount;

    /**
     * 到店时间。
     */
    private LocalDateTime visitTime;

    /**
     * 消费项明细 JSON 字符串，例如：
     * [{"name":"毛肚","price":58,"count":1},{"name":"鸭血","price":28,"count":1}]
     */
    private String items;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalDateTime visitTime) {
        this.visitTime = visitTime;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}


