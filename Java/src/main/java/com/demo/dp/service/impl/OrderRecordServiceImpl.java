package com.demo.dp.service.impl;

import com.demo.dp.domain.entity.OrderRecord;
import com.demo.dp.mapper.OrderRecordMapper;
import com.demo.dp.service.OrderRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单/消费记录服务实现类：实现与 order_record 表相关的业务逻辑。
 */
@Service
public class OrderRecordServiceImpl implements OrderRecordService {

    private final OrderRecordMapper orderRecordMapper;

    public OrderRecordServiceImpl(OrderRecordMapper orderRecordMapper) {
        this.orderRecordMapper = orderRecordMapper;
    }

    /**
     * 创建消费记录。
     */
    @Override
    @Transactional
    public OrderRecord createOrder(Long userId, Long shopId, BigDecimal amount, LocalDateTime visitTime, String itemsJson) {
        OrderRecord record = new OrderRecord();
        record.setUserId(userId);
        record.setShopId(shopId);
        record.setAmount(amount);
        record.setVisitTime(visitTime);
        record.setItems(itemsJson);
        record.setCreatedAt(LocalDateTime.now());
        orderRecordMapper.insert(record);
        return record;
    }

    /**
     * 按用户查询消费记录列表。
     */
    @Override
    public List<OrderRecord> listByUser(Long userId) {
        return orderRecordMapper.findByUserId(userId);
    }

    /**
     * 按商家查询消费记录列表。
     */
    @Override
    public List<OrderRecord> listByShop(Long shopId) {
        return orderRecordMapper.findByShopId(shopId);
    }
}


