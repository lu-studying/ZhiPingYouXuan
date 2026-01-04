package com.demo.dp.service;

import com.demo.dp.dto.DashboardStats;

/**
 * 仪表盘数据统计服务接口：定义仪表盘相关的数据统计操作。
 * 
 * <p>该接口定义了仪表盘页面所需的各种统计数据查询方法，包括：
 * <ul>
 *   <li>商家总数统计</li>
 *   <li>点评总数统计</li>
 *   <li>订单总数统计</li>
 *   <li>AI 调用统计（总数、成功率等）</li>
 *   <li>今日新增数据统计</li>
 * </ul>
 * 
 * @author System
 * @version 1.0
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据。
     * 
     * <p>该方法会聚合查询多个数据源的统计数据，返回一个包含所有统计信息的对象。
     * 统计内容包括：
     * <ul>
     *   <li>商家总数（正常状态）</li>
     *   <li>点评总数（正常状态）</li>
     *   <li>订单总数</li>
     *   <li>AI 调用总数</li>
     *   <li>AI 调用成功率（成功次数 / 总次数）</li>
     *   <li>今日新增商家数</li>
     *   <li>今日新增点评数</li>
     *   <li>今日新增订单数</li>
     * </ul>
     * 
     * <p>注意：该方法会执行多个数据库查询，如果数据量很大，可能需要优化（如使用缓存）。
     * 
     * @return 统计数据对象，包含所有统计字段
     */
    DashboardStats getStats();
}

