package com.demo.dp.controller;

import com.demo.dp.domain.entity.OrderRecord;
import com.demo.dp.dto.OrderCreateRequest;
import com.demo.dp.service.OrderRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单/消费记录相关接口。
 *
 * <p>主要能力：
 * <ul>
 *     <li>创建消费记录：POST /api/shops/{shopId}/orders</li>
 *     <li>查询当前用户的消费记录：GET /api/users/me/orders</li>
 *     <li>按商家查询消费记录：GET /api/shops/{shopId}/orders</li>
 * </ul>
 */
@RestController
@RequestMapping("/api")
public class OrderRecordController {

    private final OrderRecordService orderRecordService;

    public OrderRecordController(OrderRecordService orderRecordService) {
        this.orderRecordService = orderRecordService;
    }

    /**
     * 创建消费记录。
     *
     * <p>路径：POST /api/shops/{shopId}/orders
     * <p>认证：需要登录，从 Authentication 中获取 userId。
     */
    @PostMapping("/shops/{shopId}/orders")
    public ResponseEntity<OrderRecord> createOrder(@PathVariable Long shopId,
                                                   @RequestBody OrderCreateRequest req,
                                                   Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        OrderRecord record = orderRecordService.createOrder(
                userId,
                shopId,
                req.getAmount(),
                req.getVisitTime(),
                req.getItems()
        );
        return ResponseEntity.ok(record);
    }

    /**
     * 查询当前登录用户的消费记录列表。
     *
     * <p>路径：GET /api/users/me/orders
     */
    @GetMapping("/users/me/orders")
    public ResponseEntity<List<OrderRecord>> listMyOrders(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        List<OrderRecord> list = orderRecordService.listByUser(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * 按商家查询消费记录列表。
     *
     * <p>路径：GET /api/shops/{shopId}/orders
     * <p>说明：可用于管理端查看某个店的消费记录。
     */
    @GetMapping("/shops/{shopId}/orders")
    public ResponseEntity<Map<String, Object>> listByShop(@PathVariable Long shopId) {
        List<OrderRecord> list = orderRecordService.listByShop(shopId);
        Map<String, Object> result = new HashMap<>();
        result.put("content", list);
        result.put("total", list.size());
        return ResponseEntity.ok(result);
    }
}


