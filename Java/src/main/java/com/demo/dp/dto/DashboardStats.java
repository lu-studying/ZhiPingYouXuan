package com.demo.dp.dto;

/**
 * 仪表盘统计数据 DTO：用于返回仪表盘页面的统计数据。
 * 
 * <p>该 DTO 包含了仪表盘页面所需的所有统计数据，包括：
 * <ul>
 *   <li>商家总数、点评总数、订单总数</li>
 *   <li>AI 调用统计（总数、成功率）</li>
 *   <li>今日新增数据统计</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
public class DashboardStats {

    /**
     * 商家总数（只统计 status=1 的正常商家）。
     */
    private long shopCount;

    /**
     * 点评总数（只统计 status=1 的正常点评）。
     */
    private long reviewCount;

    /**
     * 订单总数。
     */
    private long orderCount;

    /**
     * AI 调用总次数（包括成功和失败）。
     */
    private long aiCallCount;

    /**
     * AI 调用成功率（0.0 - 1.0，例如 0.95 表示 95%）。
     * 计算公式：成功次数 / 总次数
     * 如果没有调用记录，返回 0.0。
     */
    private double aiCallSuccessRate;

    /**
     * 今日新增商家数（今天创建的商家数量）。
     */
    private long todayNewShops;

    /**
     * 今日新增点评数（今天创建的点评数量）。
     */
    private long todayNewReviews;

    /**
     * 今日新增订单数（今天创建的订单数量）。
     */
    private long todayNewOrders;

    // region 构造函数

    /**
     * 无参构造函数。
     */
    public DashboardStats() {
    }

    /**
     * 全参构造函数。
     * 
     * @param shopCount 商家总数
     * @param reviewCount 点评总数
     * @param orderCount 订单总数
     * @param aiCallCount AI 调用总次数
     * @param aiCallSuccessRate AI 调用成功率
     * @param todayNewShops 今日新增商家数
     * @param todayNewReviews 今日新增点评数
     * @param todayNewOrders 今日新增订单数
     */
    public DashboardStats(long shopCount, long reviewCount, long orderCount,
                         long aiCallCount, double aiCallSuccessRate,
                         long todayNewShops, long todayNewReviews, long todayNewOrders) {
        this.shopCount = shopCount;
        this.reviewCount = reviewCount;
        this.orderCount = orderCount;
        this.aiCallCount = aiCallCount;
        this.aiCallSuccessRate = aiCallSuccessRate;
        this.todayNewShops = todayNewShops;
        this.todayNewReviews = todayNewReviews;
        this.todayNewOrders = todayNewOrders;
    }

    // endregion

    // region getters/setters

    public long getShopCount() {
        return shopCount;
    }

    public void setShopCount(long shopCount) {
        this.shopCount = shopCount;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public long getAiCallCount() {
        return aiCallCount;
    }

    public void setAiCallCount(long aiCallCount) {
        this.aiCallCount = aiCallCount;
    }

    public double getAiCallSuccessRate() {
        return aiCallSuccessRate;
    }

    public void setAiCallSuccessRate(double aiCallSuccessRate) {
        this.aiCallSuccessRate = aiCallSuccessRate;
    }

    public long getTodayNewShops() {
        return todayNewShops;
    }

    public void setTodayNewShops(long todayNewShops) {
        this.todayNewShops = todayNewShops;
    }

    public long getTodayNewReviews() {
        return todayNewReviews;
    }

    public void setTodayNewReviews(long todayNewReviews) {
        this.todayNewReviews = todayNewReviews;
    }

    public long getTodayNewOrders() {
        return todayNewOrders;
    }

    public void setTodayNewOrders(long todayNewOrders) {
        this.todayNewOrders = todayNewOrders;
    }

    // endregion
}

