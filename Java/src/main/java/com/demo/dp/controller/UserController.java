package com.demo.dp.controller;

import com.demo.dp.domain.entity.Review;
import com.demo.dp.domain.entity.Tag;
import com.demo.dp.domain.entity.User;
import com.demo.dp.dto.WalletAmountRequest;
import com.demo.dp.service.ReviewService;
import com.demo.dp.service.TagService;
import com.demo.dp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

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
    private final TagService tagService;

    /**
     * 构造函数：注入用户服务、点评服务和标签服务。
     *
     * @param userService 用户业务服务
     * @param reviewService 点评业务服务（用于查询用户点评列表）
     * @param tagService 标签业务服务（用于查询用户标签）
     */
    public UserController(UserService userService, ReviewService reviewService, TagService tagService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.tagService = tagService;
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
    /**
     * 分页查询用户列表（仅 ADMIN 可访问）。
     * 
     * <p>使用 Spring Security 的 {@code @PreAuthorize} 注解进行权限控制，
     * 只有拥有 ROLE_ADMIN 角色的用户才能访问此接口。
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Authentication authentication) {

        log.info("查询用户列表: operator={}, page={}, size={}, keyword={}", authentication.getName(), page, size, keyword);

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
     * 查询当前登录用户的信息。
     * 
     * <p>路径：GET /api/users/me
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：查询当前登录用户的详细信息。
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
     * @param authentication 认证信息，用于获取当前用户ID
     * @return 用户对象，HTTP 200 状态码；如果用户不存在，返回 HTTP 404
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("查询当前用户信息: userId={}", userId);

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: userId={}", userId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userOpt.get());
    }

    @GetMapping("/me/wallet")
    public ResponseEntity<Map<String, Object>> getMyWallet(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        BigDecimal balance = userService.getBalance(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/me/wallet/recharge")
    public ResponseEntity<Map<String, Object>> recharge(Authentication authentication,
                                                        @RequestBody WalletAmountRequest req) {
        Long userId = Long.parseLong(authentication.getName());
        BigDecimal balance = userService.recharge(userId, req == null ? null : req.getAmount());
        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance);
        result.put("message", "充值成功");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/me/wallet/withdraw")
    public ResponseEntity<Map<String, Object>> withdraw(Authentication authentication,
                                                        @RequestBody WalletAmountRequest req) {
        Long userId = Long.parseLong(authentication.getName());
        BigDecimal balance = userService.withdraw(userId, req == null ? null : req.getAmount());
        Map<String, Object> result = new HashMap<>();
        result.put("balance", balance);
        result.put("message", "提现成功");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        try {
            userService.deleteUser(id);
            log.info("删除用户成功: userId={}, operator={}", id, authentication != null ? authentication.getName() : "unknown");
            return ResponseEntity.ok(java.util.Map.of("message", "删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(java.util.Map.of("code", 404, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("code", 500, "message", "删除用户失败: " + e.getMessage()));
        }
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
     * 查询当前登录用户的点评列表。
     * 
     * <p>路径：GET /api/users/me/reviews
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：查询当前登录用户的所有点评列表（分页）。
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
     * @param authentication 认证信息，用于获取当前用户ID
     * @param page 页码（从0开始），可选，默认0
     * @param size 每页大小，可选，默认10
     * @return 点评列表（分页结果），HTTP 200 状态码
     */
    @GetMapping("/me/reviews")
    public ResponseEntity<Map<String, Object>> getMyReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long userId = Long.parseLong(authentication.getName());
        log.info("查询当前用户点评列表: userId={}, page={}, size={}", userId, page, size);

        // 调用服务层查询用户点评列表
        List<Review> reviews = reviewService.listByUser(userId, page, size);
        long total = reviewService.countByUserId(userId);

        // 构建响应结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", reviews);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me/liked-reviews")
    public ResponseEntity<Map<String, Object>> getMyLikedReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = Long.parseLong(authentication.getName());
        List<Review> reviews = reviewService.listLikedByUser(userId, page, size);
        long total = reviewService.countLikedByUser(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("content", reviews);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
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

    /**
     * 查询指定用户的标签列表。
     * 
     * <p>路径：GET /api/users/{id}/tags
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：查询指定用户已绑定的标签列表。
     * 
     * <p>路径参数：
     * <ul>
     *   <li>id：用户ID</li>
     * </ul>
     * 
     * <p>响应示例：
     * <pre>
     * [
     *   {
     *     "id": 1,
     *     "name": "爱吃辣",
     *     "type": "user",
     *     "createdAt": "2025-12-15T10:00:00"
     *   },
     *   {
     *     "id": 2,
     *     "name": "环境控",
     *     "type": "user",
     *     "createdAt": "2025-12-15T10:00:00"
     *   }
     * ]
     * </pre>
     * 
     * @param id 用户ID
     * @return 标签列表，HTTP 200 状态码
     */
    @GetMapping("/{id}/tags")
    public ResponseEntity<List<Tag>> getUserTags(@PathVariable Long id) {
        log.info("查询用户标签列表: userId={}", id);
        List<Tag> tags = tagService.listTagsOfUser(id);
        return ResponseEntity.ok(tags);
    }

    /**
     * 更新当前登录用户的信息。
     * 
     * <p>路径：PUT /api/users/me
     * <p>认证：需要用户登录（JWT）。
     * <p>功能：更新当前登录用户的昵称、头像等信息。
     * 
     * <p>请求体示例：
     * <pre>
     * {
     *   "nickname": "新昵称",
     *   "avatar": "https://example.com/avatar.jpg"
     * }
     * </pre>
     * 
     * <p>响应示例：
     * <pre>
     * {
     *   "id": 1,
     *   "mobile": "13800138000",
     *   "email": null,
     *   "nickname": "新昵称",
     *   "avatar": "https://example.com/avatar.jpg",
     *   "status": 1,
     *   "createdAt": "2025-12-15T10:00:00",
     *   "updatedAt": "2025-12-15T11:00:00"
     * }
     * </pre>
     * 
     * @param authentication 认证信息，用于获取当前用户ID
     * @param user 用户对象（包含需要更新的字段）
     * @return 更新后的用户对象，HTTP 200 状态码；如果用户不存在，返回 HTTP 404
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(
            Authentication authentication,
            @RequestBody User user) {
        Long userId = Long.parseLong(authentication.getName());
        log.info("更新当前用户信息: userId={}, nickname={}", userId, user.getNickname());

        // 设置用户ID，确保只能更新自己的信息
        user.setId(userId);

        try {
            User updated = userService.updateUser(user);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("更新用户信息失败: {}", e.getMessage());
            // 返回错误信息，前端可以显示给用户
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            // 根据错误类型返回不同的状态码
            if (e.getMessage().contains("昵称已被使用")) {
                return ResponseEntity.status(400).body(error); // 400 Bad Request
            } else if (e.getMessage().contains("用户不存在")) {
                return ResponseEntity.status(404).body(error); // 404 Not Found
            } else {
                return ResponseEntity.status(400).body(error); // 400 Bad Request
            }
        }
    }
}

