package com.demo.dp.controller;

import com.demo.dp.domain.entity.AiCallLog;
import com.demo.dp.service.AiCallLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 调用日志接口控制器。
 * 
 * <p>该控制器提供 AI 调用日志查询接口，用于管理端监控和分析 AI 调用情况，包括：
 * <ul>
 *   <li>分页查询 AI 调用日志列表（支持多条件筛选）</li>
 *   <li>统计符合条件的日志总数</li>
 * </ul>
 * 
 * <p>所有接口都需要用户登录（JWT 认证）。
 * 
 * @author System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/ai-logs")
public class AiCallLogController {

    /**
     * 日志记录器，用于输出调用链路和错误信息。
     */
    private static final Logger log = LoggerFactory.getLogger(AiCallLogController.class);

    private final AiCallLogService aiCallLogService;

    /**
     * 构造函数：注入 AI 调用日志服务。
     *
     * @param aiCallLogService AI 调用日志业务服务
     */
    public AiCallLogController(AiCallLogService aiCallLogService) {
        this.aiCallLogService = aiCallLogService;
    }

    /**
     * 分页查询 AI 调用日志列表（支持多条件筛选）。
     * 
     * <p>路径：GET /api/ai-logs
     * <p>认证：需要用户登录（JWT），从 Authentication 读取 userId（可选，用于日志记录）。
     * <p>功能：分页查询 AI 调用日志列表，支持按调用类型、状态、用户ID筛选。
     * 
     * <p>查询参数：
     * <ul>
     *   <li>page（可选，默认0）：页码，从0开始</li>
     *   <li>size（可选，默认10）：每页大小</li>
     *   <li>type（可选）：调用类型筛选，可选值：generate（生成草稿）、recommend（推荐）</li>
     *   <li>status（可选）：状态筛选，可选值：1（成功）、0（失败）</li>
     *   <li>userId（可选）：用户ID筛选，只查询指定用户的调用记录</li>
     * </ul>
     * 
     * <p>响应示例：
     * <pre>
     * {
     *   "content": [
     *     {
     *       "id": 1,
     *       "userId": 3,
     *       "type": "generate",
     *       "prompt": "生成点评草稿...",
     *       "responseRef": "生成的草稿内容...",
     *       "latencyMs": 1200,
     *       "status": 1,
     *       "createdAt": "2025-12-15T10:00:00"
     *     }
     *   ],
     *   "total": 1500,
     *   "page": 0,
     *   "size": 10
     * }
     * </pre>
     * 
     * @param page 页码（从0开始），可选，默认0
     * @param size 每页大小，可选，默认10
     * @param type 调用类型筛选（generate/recommend），可选
     * @param status 状态筛选（1=成功，0=失败），可选
     * @param userId 用户ID筛选，可选
     * @param authentication Spring Security 认证对象，用于获取当前登录用户信息（可选）
     * @return AI 调用日志列表（分页结果），HTTP 200 状态码
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> listLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            Authentication authentication) {
        
        // 记录调用日志（可选）
        if (authentication != null) {
            Long currentUserId = Long.parseLong(authentication.getName());
            log.info("用户 {} 查询 AI 调用日志: page={}, size={}, type={}, status={}, userId={}", 
                    currentUserId, page, size, type, status, userId);
        } else {
            log.info("匿名用户查询 AI 调用日志: page={}, size={}, type={}, status={}, userId={}", 
                    page, size, type, status, userId);
        }

        // 调用服务层查询日志列表
        List<AiCallLog> logs = aiCallLogService.listLogs(page, size, type, status, userId);
        long total = aiCallLogService.countLogs(type, status, userId);

        // 构建响应结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", logs);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }
}

