package com.demo.dp.service;

import com.demo.dp.domain.entity.OrderRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单/消费记录服务接口：封装与 order_record 表相关的业务逻辑。
 *
 * <p>主要功能：
 * <ul>
 *     <li>创建消费记录</li>
 *     <li>按用户查询消费记录列表</li>
 *     <li>按商家查询消费记录列表</li>
 * </ul>
 */
public interface OrderRecordService {

    /**
     * 创建一条消费记录。
     *
     * @param userId    用户ID（当前登录用户）
     * @param shopId    商家ID
     * @param amount    消费金额
     * @param visitTime 到店时间
     * @param itemsJson 消费项明细 JSON 字符串
     * @return 创建成功的订单记录（包含ID）
     */
    OrderRecord createOrder(Long userId, Long shopId, BigDecimal amount, LocalDateTime visitTime, String itemsJson);

    /**
     * 按用户查询消费记录列表（按时间倒序）。
     *
     * @param userId 用户ID
     * @return 消费记录列表
     */
    List<OrderRecord> listByUser(Long userId);

    /**
     * 按商家查询消费记录列表（按时间倒序）。
     *
     * @param shopId 商家ID
     * @return 消费记录列表
     */
    List<OrderRecord> listByShop(Long shopId);
}


