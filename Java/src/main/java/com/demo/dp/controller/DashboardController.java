package com.demo.dp.controller;

import com.demo.dp.dto.DashboardStats;
import com.demo.dp.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘数据统计接口控制器。
 * 
 * <p>该控制器提供仪表盘页面所需的数据统计接口，包括：
 * <ul>
 *   <li>商家总数、点评总数、订单总数</li>
 *   <li>AI 调用统计（总数、成功率）</li>
 *   <li>今日新增数据统计</li>
 * </ul>
 * 
 * <p>所有接口都需要用户登录（JWT 认证）。
 * 
 * @author System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    /**
     * 日志记录器，用于输出调用链路和错误信息。
     */
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    /**
     * 构造函数：注入仪表盘数据统计服务。
     *
     * @param dashboardService 仪表盘数据统计服务
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 获取仪表盘统计数据。
     * 
     * <p>路径：GET /api/dashboard/stats
     * <p>认证：需要用户登录（JWT），从 Authentication 读取 userId（可选，用于日志记录）。
     * <p>功能：聚合查询多个数据源的统计数据，返回仪表盘页面所需的所有统计信息。
     * 
     * <p>返回的统计数据包括：
     * <ul>
     *   <li>shopCount：商家总数（只统计正常状态）</li>
     *   <li>reviewCount：点评总数（只统计正常状态）</li>
     *   <li>orderCount：订单总数</li>
     *   <li>aiCallCount：AI 调用总次数</li>
     *   <li>aiCallSuccessRate：AI 调用成功率（0.0 - 1.0）</li>
     *   <li>todayNewShops：今日新增商家数</li>
     *   <li>todayNewReviews：今日新增点评数</li>
     *   <li>todayNewOrders：今日新增订单数</li>
     * </ul>
     * 
     * <p>响应示例：
     * <pre>
     * {
     *   "shopCount": 100,
     *   "reviewCount": 5000,
     *   "orderCount": 2000,
     *   "aiCallCount": 1500,
     *   "aiCallSuccessRate": 0.95,
     *   "todayNewShops": 5,
     *   "todayNewReviews": 50,
     *   "todayNewOrders": 20
     * }
     * </pre>
     * 
     * @param authentication Spring Security 认证对象，用于获取当前登录用户信息（可选）
     * @return 统计数据对象，HTTP 200 状态码
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getStats(Authentication authentication) {
        // 记录调用日志（可选）
        if (authentication != null) {
            Long userId = Long.parseLong(authentication.getName());
            log.info("用户 {} 查询仪表盘统计数据", userId);
        } else {
            log.info("匿名用户查询仪表盘统计数据");
        }

        // 调用服务层获取统计数据
        DashboardStats stats = dashboardService.getStats();
        
        return ResponseEntity.ok(stats);
    }
}

