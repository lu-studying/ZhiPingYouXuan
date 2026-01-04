package com.demo.dp.mapper;

import com.demo.dp.domain.entity.OrderRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消费记录 Mapper 接口。
 * SQL 定义在 mapper/OrderRecordMapper.xml。
 */
@Mapper
public interface OrderRecordMapper {
    /**
     * 根据用户 ID 查询消费记录
     */
    List<OrderRecord> findByUserId(@Param("userId") Long userId);

    /**
     * 根据商家 ID 查询消费记录
     */
    List<OrderRecord> findByShopId(@Param("shopId") Long shopId);

    /**
     * 插入新消费记录
     */
    int insert(OrderRecord orderRecord);

    /**
     * 根据 ID 查询消费记录
     */
    OrderRecord findById(@Param("id") Long id);

    /**
     * 查询用户在某店最近一条消费记录（用于生成点评上下文）。
     *
     * @param userId 用户ID
     * @param shopId 商家ID
     * @return 最近一条消费记录，可能为 null
     */
    OrderRecord findLatestByUserAndShop(@Param("userId") Long userId,
                                        @Param("shopId") Long shopId);

    /**
     * 统计所有订单总数。
     *
     * @return 订单总数
     */
    long countAll();

    /**
     * 统计指定日期范围内创建的订单总数。
     *
     * @param startDate 开始时间（包含）
     * @param endDate 结束时间（包含）
     * @return 订单总数
     */
    long countByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                          @Param("endDate") java.time.LocalDateTime endDate);
}

