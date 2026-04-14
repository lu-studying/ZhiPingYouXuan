package com.demo.dp.dto;

import java.math.BigDecimal;

/**
 * 钱包金额操作请求。
 */
public class WalletAmountRequest {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
