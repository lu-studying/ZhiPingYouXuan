package com.demo.dp.controller;

import com.demo.dp.domain.entity.Review;
import com.demo.dp.domain.entity.User;
import com.demo.dp.service.ReviewService;
import com.demo.dp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户管理接口控制器。
 * 
 * <p>该控制器提供用户管理相关的接口，包括：
 * <ul>
 *   <li>用户列表查询（分页、搜索）</li>
 *   <li>用户详情查询</li>
 *   <li>用户点评列表查询</li>
 * </ul>
 * 
 * <p>注意：该控制器与 {@link AuthController} 不同，AuthController 负责认证（登录/注册），
 * 而 UserController 负责用户信息查询和管理。
 * 
 * <p>所有接口都需要用户登录（JWT 认证）。
 * 
 * @author System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 日志记录器，用于输出调用链路和错误信息。
     */
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ReviewService reviewService;

    /**
     * 构造函数：注入用户服务和点评服务。
     *
     * @param userService 用户业务服务
     * @param reviewService 点评业务服务（用于查询用户点评列表）
     */
    public UserController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    /**
     * 分页查询用户列表（支持关键词搜索）。
     * 
     * <p>路径：GET /api/users
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：分页查询用户列表，支持在手机号和邮箱中搜索关键词。
     * 
     * <p>查询参数：
     * <ul>
     *   <li>page（可选，默认0）：页码，从0开始</li>
     *   <li>size（可选，默认10）：每页大小</li>
     *   <li>keyword（可选）：搜索关键词，会在手机号和邮箱中模糊匹配</li>
     * </ul>
     * 
     * <p>响应示例：
     * <pre>
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "mobile": "13800138000",
     *       "email": null,
     *       "nickname": "用户1",
     *       "avatar": null,
     *       "status": 1,
     *       "createdAt": "2025-12-15T10:00:00"
     *     }
     *   ],
     *   "total": 100,
     *   "page": 0,
     *   "size": 10
     * }
     * </pre>
     * 
     * @param page 页码（从0开始），可选，默认0
     * @param size 每页大小，可选，默认10
     * @param keyword 搜索关键词（在手机号和邮箱中搜索），可选
     * @return 用户列表（分页结果），HTTP 200 状态码
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        log.info("查询用户列表: page={}, size={}, keyword={}", page, size, keyword);

        // 调用服务层查询用户列表
        List<User> users = userService.listUsers(page, size, keyword);
        long total = userService.countUsers(keyword);

        // 构建响应结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", users);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }

    /**
     * 根据用户ID查询用户详情。
     * 
     * <p>路径：GET /api/users/{id}
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：根据用户ID查询用户详细信息。
     * 
     * <p>路径参数：
     * <ul>
     *   <li>id：用户ID</li>
     * </ul>
     * 
     * <p>响应示例：
     * <pre>
     * {
     *   "id": 1,
     *   "mobile": "13800138000",
     *   "email": null,
     *   "nickname": "用户1",
     *   "avatar": null,
     *   "status": 1,
     *   "createdAt": "2025-12-15T10:00:00",
     *   "updatedAt": "2025-12-15T10:00:00"
     * }
     * </pre>
     * 
     * @param id 用户ID
     * @return 用户对象，HTTP 200 状态码；如果用户不存在，返回 HTTP 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("查询用户详情: userId={}", id);

        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: userId={}", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userOpt.get());
    }

    /**
     * 查询指定用户的点评列表。
     * 
     * <p>路径：GET /api/users/{id}/reviews
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：查询指定用户的所有点评列表（分页）。
     * 
     * <p>路径参数：
     * <ul>
     *   <li>id：用户ID</li>
     * </ul>
     * 
     * <p>查询参数：
     * <ul>
     *   <li>page（可选，默认0）：页码，从0开始</li>
     *   <li>size（可选，默认10）：每页大小</li>
     *   </ul>
     * 
     * <p>响应示例：
     * <pre>
     * {
     *   "content": [
     *     {
     *       "id": 10,
     *       "shopId": 1,
     *       "userId": 1,
     *       "rating": 5,
     *       "content": "很好吃",
     *       "images": "[]",
     *       "isAiGenerated": false,
     *       "likeCount": 10,
     *       "status": 1,
     *       "createdAt": "2025-12-15T10:00:00"
     *     }
     *   ],
     *   "total": 50,
     *   "page": 0,
     *   "size": 10
     * }
     * </pre>
     * 
     * @param id 用户ID
     * @param page 页码（从0开始），可选，默认0
     * @param size 每页大小，可选，默认10
     * @return 点评列表（分页结果），HTTP 200 状态码
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<Map<String, Object>> getUserReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("查询用户点评列表: userId={}, page={}, size={}", id, page, size);

        // 调用服务层查询用户点评列表
        List<Review> reviews = reviewService.listByUser(id, page, size);
        long total = reviewService.countByUserId(id);

        // 构建响应结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", reviews);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }
}

