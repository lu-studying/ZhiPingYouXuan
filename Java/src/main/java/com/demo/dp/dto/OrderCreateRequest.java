package com.demo.dp.dto;

import java.time.LocalDateTime;

/**
 * 创建消费记录请求 DTO。
 */
public class OrderCreateRequest {

    /**
     * 下单项 JSON 字符串，例如：
     * [{"menuId":1,"quantity":2},{"menuId":5,"quantity":1}]
     */
    private String items;

    /**
     * 到店时间。
     */
    private LocalDateTime visitTime;

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public LocalDateTime getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(LocalDateTime visitTime) {
        this.visitTime = visitTime;
    }

}


