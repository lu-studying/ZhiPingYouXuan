package com.demo.dp.service.impl;

import com.demo.dp.dto.DashboardStats;
import com.demo.dp.mapper.AiCallLogMapper;
import com.demo.dp.mapper.OrderRecordMapper;
import com.demo.dp.mapper.ReviewMapper;
import com.demo.dp.mapper.ShopMapper;
import com.demo.dp.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 仪表盘数据统计服务实现类：实现仪表盘相关的数据统计业务逻辑。
 * 
 * <p>该类负责聚合查询多个数据源的统计数据，包括：
 * <ul>
 *   <li>商家总数统计（只统计正常状态的商家）</li>
 *   <li>点评总数统计（只统计正常状态的点评）</li>
 *   <li>订单总数统计</li>
 *   <li>AI 调用统计（总数、成功率）</li>
 *   <li>今日新增数据统计</li>
 * </ul>
 * 
 * <p>注意：该方法会执行多个数据库查询，如果数据量很大，建议后续使用缓存优化。
 * 
 * @author System
 * @version 1.0
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    /**
     * 日志记录器，用于输出调用链路和错误信息。
     */
    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);

    private final ShopMapper shopMapper;
    private final ReviewMapper reviewMapper;
    private final OrderRecordMapper orderRecordMapper;
    private final AiCallLogMapper aiCallLogMapper;

    /**
     * 构造函数：注入各个数据访问层的 Mapper。
     *
     * @param shopMapper 商家数据访问层
     * @param reviewMapper 点评数据访问层
     * @param orderRecordMapper 订单数据访问层
     * @param aiCallLogMapper AI 调用日志数据访问层
     */
    public DashboardServiceImpl(ShopMapper shopMapper,
                                ReviewMapper reviewMapper,
                                OrderRecordMapper orderRecordMapper,
                                AiCallLogMapper aiCallLogMapper) {
        this.shopMapper = shopMapper;
        this.reviewMapper = reviewMapper;
        this.orderRecordMapper = orderRecordMapper;
        this.aiCallLogMapper = aiCallLogMapper;
    }

    /**
     * 获取仪表盘统计数据。
     * 
     * <p>该方法会聚合查询多个数据源的统计数据：
     * <ol>
     *   <li>查询商家总数（status=1）</li>
     *   <li>查询点评总数（status=1）</li>
     *   <li>查询订单总数</li>
     *   <li>查询 AI 调用总数和成功次数，计算成功率</li>
     *   <li>查询今日新增数据（商家、点评、订单）</li>
     * </ol>
     * 
     * <p>注意：如果某个统计项查询失败，会记录日志但不影响其他统计项的返回。
     * 
     * @return 统计数据对象，包含所有统计字段
     */
    @Override
    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();

        try {
            // 1. 查询商家总数（只统计 status=1 的正常商家）
            long shopCount = shopMapper.countByConditions(null, null, null, null);
            stats.setShopCount(shopCount);
            log.debug("查询商家总数: {}", shopCount);
        } catch (Exception e) {
            log.error("查询商家总数失败", e);
            stats.setShopCount(0);
        }

        try {
            // 2. 查询点评总数（需要扩展 ReviewMapper 支持全量统计）
            // 注意：当前 ReviewMapper 只有按商家统计的方法，需要新增全量统计方法
            // 暂时使用一个较大的 shopId 来估算，或者扩展 Mapper
            // 这里先使用一个临时方案：遍历所有商家统计（性能较差，后续需要优化）
            long reviewCount = reviewMapper.countAll();
            stats.setReviewCount(reviewCount);
            log.debug("查询点评总数: {}", reviewCount);
        } catch (Exception e) {
            log.error("查询点评总数失败", e);
            stats.setReviewCount(0);
        }

        try {
            // 3. 查询订单总数（需要扩展 OrderRecordMapper 支持全量统计）
            long orderCount = orderRecordMapper.countAll();
            stats.setOrderCount(orderCount);
            log.debug("查询订单总数: {}", orderCount);
        } catch (Exception e) {
            log.error("查询订单总数失败", e);
            stats.setOrderCount(0);
        }

        try {
            // 4. 查询 AI 调用统计
            long aiCallCount = aiCallLogMapper.countAll();
            long aiCallSuccessCount = aiCallLogMapper.countByStatus(1);
            
            // 计算成功率：成功次数 / 总次数
            double aiCallSuccessRate = aiCallCount > 0 
                    ? (double) aiCallSuccessCount / aiCallCount 
                    : 0.0;
            
            stats.setAiCallCount(aiCallCount);
            stats.setAiCallSuccessRate(aiCallSuccessRate);
            log.debug("查询 AI 调用统计: 总数={}, 成功数={}, 成功率={}", 
                    aiCallCount, aiCallSuccessCount, aiCallSuccessRate);
        } catch (Exception e) {
            log.error("查询 AI 调用统计失败", e);
            stats.setAiCallCount(0);
            stats.setAiCallSuccessRate(0.0);
        }

        try {
            // 5. 查询今日新增数据
            // 计算今天的开始时间（00:00:00）和结束时间（23:59:59）
            LocalDate today = LocalDate.now();
            LocalDateTime todayStart = LocalDateTime.of(today, LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(today, LocalTime.MAX);

            // 今日新增商家数
            long todayNewShops = shopMapper.countByDateRange(todayStart, todayEnd);
            stats.setTodayNewShops(todayNewShops);

            // 今日新增点评数
            long todayNewReviews = reviewMapper.countByDateRange(todayStart, todayEnd);
            stats.setTodayNewReviews(todayNewReviews);

            // 今日新增订单数
            long todayNewOrders = orderRecordMapper.countByDateRange(todayStart, todayEnd);
            stats.setTodayNewOrders(todayNewOrders);

            log.debug("查询今日新增数据: 商家={}, 点评={}, 订单={}", 
                    todayNewShops, todayNewReviews, todayNewOrders);
        } catch (Exception e) {
            log.error("查询今日新增数据失败", e);
            stats.setTodayNewShops(0);
            stats.setTodayNewReviews(0);
            stats.setTodayNewOrders(0);
        }

        return stats;
    }
}

