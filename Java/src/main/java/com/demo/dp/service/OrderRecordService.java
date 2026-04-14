package com.demo.dp.service;

import com.demo.dp.domain.entity.OrderRecord;

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
     * 创建一条待支付订单（金额由服务端根据菜单价格计算）。
     *
     * @param userId    用户ID（当前登录用户）
     * @param shopId    商家ID
     * @param visitTime 到店时间
     * @param itemsJson 下单项 JSON（menuId + quantity）
     * @return 创建成功的订单记录（支付状态=待支付）
     */
    OrderRecord createOrder(Long userId, Long shopId, LocalDateTime visitTime, String itemsJson);

    /**
     * 按用户查询消费记录列表（按时间倒序）。
     *
     * @param userId 用户ID
     * @return 消费记录列表
     */
    List<OrderRecord> listByUser(Long userId, Integer payStatus);

    /**
     * 按商家查询消费记录列表（按时间倒序）。
     *
     * @param shopId 商家ID
     * @return 消费记录列表
     */
    List<OrderRecord> listByShop(Long shopId, Integer payStatus);

    /**
     * 模拟支付。
     */
    OrderRecord mockPay(Long userId, Long orderId, String payMethod);

    /**
     * 取消订单（仅待支付允许取消）。
     */
    OrderRecord cancelOrder(Long userId, Long orderId);
}


