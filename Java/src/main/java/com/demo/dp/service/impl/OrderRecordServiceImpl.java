package com.demo.dp.service.impl;

import com.demo.dp.domain.entity.Menu;
import com.demo.dp.domain.entity.OrderRecord;
import com.demo.dp.domain.entity.User;
import com.demo.dp.domain.entity.WalletTxn;
import com.demo.dp.mapper.MenuMapper;
import com.demo.dp.mapper.OrderRecordMapper;
import com.demo.dp.mapper.UserMapper;
import com.demo.dp.mapper.WalletTxnMapper;
import com.demo.dp.service.OrderRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单/消费记录服务实现类：实现与 order_record 表相关的业务逻辑。
 */
@Service
public class OrderRecordServiceImpl implements OrderRecordService {

    private final OrderRecordMapper orderRecordMapper;
    private final MenuMapper menuMapper;
    private final UserMapper userMapper;
    private final WalletTxnMapper walletTxnMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderRecordServiceImpl(OrderRecordMapper orderRecordMapper, MenuMapper menuMapper, UserMapper userMapper, WalletTxnMapper walletTxnMapper) {
        this.orderRecordMapper = orderRecordMapper;
        this.menuMapper = menuMapper;
        this.userMapper = userMapper;
        this.walletTxnMapper = walletTxnMapper;
    }

    /**
     * 创建待支付订单（金额由后端根据菜单价格计算）。
     */
    @Override
    @Transactional
    public OrderRecord createOrder(Long userId, Long shopId, LocalDateTime visitTime, String itemsJson) {
        if (userId == null || shopId == null) {
            throw new IllegalArgumentException("用户或商家信息不完整");
        }
        List<ItemLine> lines = parseAndValidateItems(shopId, itemsJson);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("下单项不能为空");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (ItemLine line : lines) {
            total = total.add(line.price.multiply(BigDecimal.valueOf(line.quantity)));
        }

        OrderRecord record = new OrderRecord();
        record.setUserId(userId);
        record.setShopId(shopId);
        record.setOrderNo(generateOrderNo());
        record.setAmount(total.setScale(2, RoundingMode.HALF_UP));
        record.setPayStatus(0);
        record.setVisitTime(visitTime != null ? visitTime : LocalDateTime.now());
        record.setItems(buildOrderItemsJson(lines));
        record.setCreatedAt(LocalDateTime.now());
        orderRecordMapper.insert(record);
        return record;
    }

    /**
     * 按用户查询消费记录列表。
     */
    @Override
    public List<OrderRecord> listByUser(Long userId, Integer payStatus) {
        return orderRecordMapper.findByUserId(userId, payStatus);
    }

    /**
     * 按商家查询消费记录列表。
     */
    @Override
    public List<OrderRecord> listByShop(Long shopId, Integer payStatus) {
        return orderRecordMapper.findByShopId(shopId, payStatus);
    }

    @Override
    @Transactional
    public OrderRecord mockPay(Long userId, Long orderId, String payMethod) {
        OrderRecord order = requireMyOrder(userId, orderId);
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            return order;
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 2) {
            throw new RuntimeException("订单已取消，无法支付");
        }
        User user = userMapper.findByIdForUpdate(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        BigDecimal beforeBalance = user.getBalance() == null ? BigDecimal.ZERO : user.getBalance();
        BigDecimal orderAmount = order.getAmount() == null ? BigDecimal.ZERO : order.getAmount();
        if (beforeBalance.compareTo(orderAmount) < 0) {
            throw new IllegalArgumentException("账户余额不足，请先充值");
        }
        BigDecimal afterBalance = beforeBalance.subtract(orderAmount);
        userMapper.changeBalance(userId, orderAmount.negate());

        String method = (payMethod == null || payMethod.isBlank()) ? "MOCK_WECHAT" : payMethod;
        String txnNo = "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        WalletTxn txn = new WalletTxn();
        txn.setUserId(userId);
        txn.setType("PAY");
        txn.setAmount(orderAmount.negate());
        txn.setBalanceBefore(beforeBalance);
        txn.setBalanceAfter(afterBalance);
        txn.setBizNo(txnNo);
        txn.setRemark("订单支付扣款:" + order.getOrderNo());
        walletTxnMapper.insert(txn);

        orderRecordMapper.markPaid(orderId, method, txnNo);
        return orderRecordMapper.findById(orderId);
    }

    @Override
    @Transactional
    public OrderRecord cancelOrder(Long userId, Long orderId) {
        OrderRecord order = requireMyOrder(userId, orderId);
        if (order.getPayStatus() != null && order.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付，无法取消");
        }
        if (order.getPayStatus() != null && order.getPayStatus() == 2) {
            return order;
        }
        orderRecordMapper.markCanceled(orderId);
        return orderRecordMapper.findById(orderId);
    }

    private OrderRecord requireMyOrder(Long userId, Long orderId) {
        OrderRecord order = orderRecordMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        return order;
    }

    private List<ItemLine> parseAndValidateItems(Long shopId, String itemsJson) {
        try {
            JsonNode root = objectMapper.readTree(itemsJson);
            if (root == null || !root.isArray()) {
                throw new IllegalArgumentException("下单项格式错误");
            }
            List<ItemLine> lines = new ArrayList<>();
            for (JsonNode node : root) {
                Long menuId = node.has("menuId") ? node.get("menuId").asLong() : null;
                int qty = node.has("quantity") ? node.get("quantity").asInt() : 0;
                if (menuId == null || qty <= 0) {
                    throw new IllegalArgumentException("下单项包含非法 menuId 或 quantity");
                }
                Menu menu = menuMapper.findById(menuId);
                if (menu == null || menu.getShopId() == null || !menu.getShopId().equals(shopId)) {
                    throw new IllegalArgumentException("菜品不存在或不属于当前商家");
                }
                if (menu.getPrice() == null) {
                    throw new IllegalArgumentException("菜品价格异常");
                }
                lines.add(new ItemLine(menuId, menu.getName(), menu.getPrice(), qty));
            }
            return lines;
        } catch (Exception e) {
            throw new IllegalArgumentException("下单项解析失败: " + e.getMessage());
        }
    }

    private String buildOrderItemsJson(List<ItemLine> lines) {
        try {
            return objectMapper.writeValueAsString(lines);
        } catch (Exception e) {
            throw new RuntimeException("订单明细序列化失败");
        }
    }

    private String generateOrderNo() {
        return "DP" + System.currentTimeMillis();
    }

    private static class ItemLine {
        public Long menuId;
        public String name;
        public BigDecimal price;
        public Integer quantity;

        ItemLine(Long menuId, String name, BigDecimal price, Integer quantity) {
            this.menuId = menuId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
    }
}


