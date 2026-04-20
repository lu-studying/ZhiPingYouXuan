package com.demo.dp.controller;

import com.demo.dp.domain.entity.Review;
import com.demo.dp.domain.entity.Shop;
import com.demo.dp.dto.AiDraftRequest;
import com.demo.dp.dto.AiDraftResponse;
import com.demo.dp.dto.AiDraftTaskStartResponse;
import com.demo.dp.dto.AiDraftTaskStatusResponse;
import com.demo.dp.dto.AiRecommendItemResponse;
import com.demo.dp.dto.ReviewCreateRequest;
import com.demo.dp.mapper.ShopMapper;
import com.demo.dp.service.AiDraftTaskService;
import com.demo.dp.service.AiReviewService;
import com.demo.dp.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    /**
     * 商家点评相关接口。
     * 后续可增加排序、过滤、AI 推荐、点赞等参数。
     */
@RestController
@RequestMapping("/api/shops/{shopId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AiReviewService aiReviewService;
    private final AiDraftTaskService aiDraftTaskService;
    private final ShopMapper shopMapper;

    public ReviewController(ReviewService reviewService, AiReviewService aiReviewService, AiDraftTaskService aiDraftTaskService, ShopMapper shopMapper) {
        this.reviewService = reviewService;
        this.aiReviewService = aiReviewService;
        this.aiDraftTaskService = aiDraftTaskService;
        this.shopMapper = shopMapper;
    }

    private boolean hasPermissionForShop(Long shopId, Authentication authentication) {
        if (shopId == null || authentication == null) {
            return false;
        }
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return true;
        }
        boolean isMerchant = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_MERCHANT".equals(a.getAuthority()));
        if (!isMerchant) {
            return false;
        }
        Shop shop = shopMapper.findById(shopId);
        if (shop == null) {
            return false;
        }
        Long currentUserId = Long.parseLong(authentication.getName());
        return shop.getOwnerUserId() != null && shop.getOwnerUserId().equals(currentUserId);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@PathVariable Long shopId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      Authentication authentication) {
        Long currentUserId = authentication == null ? null : Long.parseLong(authentication.getName());
        List<Review> reviews = reviewService.listByShop(shopId, currentUserId, page, size);
        long total = reviewService.countByShopId(shopId);
        Map<String, Object> result = new HashMap<>();
        result.put("content", reviews);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Review> create(@PathVariable Long shopId,
                                         @RequestBody ReviewCreateRequest req,
                                         Authentication authentication) {
        // 从 JWT 解析 userId
        Long userId = Long.parseLong(authentication.getName());
        Review saved = reviewService.createReview(userId, shopId, req.getRating(), req.getContent(), req.getImages());
        return ResponseEntity.ok(saved);
    }

    /**
     * 点赞指定点评。
     *
     * <p>路径：POST /api/shops/{shopId}/reviews/{reviewId}/like
     * <p>认证：需要用户登录（JWT），从 Authentication 读取 userId。
     * <p>说明：MVP 实现为简单的 like_count 自增，未做防重复点赞。
     */
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<?> like(@PathVariable Long shopId,
                                  @PathVariable Long reviewId,
                                  Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean liked = reviewService.likeReview(userId, reviewId);
        return ResponseEntity.ok(java.util.Map.of(
                "message", liked ? "点赞成功" : "已点赞",
                "liked", liked
        ));
    }

    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<?> unlike(@PathVariable Long shopId,
                                    @PathVariable Long reviewId,
                                    Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean unliked = reviewService.unlikeReview(userId, reviewId);
        return ResponseEntity.ok(java.util.Map.of(
                "message", unliked ? "已取消点赞" : "未点赞",
                "liked", !unliked
        ));
    }

    /**
     * AI 辅助生成点评草稿。
     *
     * <p>路径：POST /api/shops/{shopId}/reviews/ai-draft
     * <p>认证：需要用户登录（JWT），从 Authentication 读取 userId。
     * <p>入参：AiDraftRequest（偏好、期望长度可选）
     * <p>出参：AiDraftResponse（草稿文本）
     */
    @PostMapping("/ai-draft")
    public ResponseEntity<AiDraftResponse> generateDraft(@PathVariable Long shopId,
                                                         @RequestBody AiDraftRequest request,
                                                         Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        String preference = request.getPreference();
        String draft = aiReviewService.generateDraft(userId, shopId, preference);
        return ResponseEntity.ok(new AiDraftResponse(draft));
    }

    /**
     * 异步启动 AI 草稿生成任务。
     *
     * <p>路径：POST /api/shops/{shopId}/reviews/ai-draft/tasks
     * <p>出参：taskId（用于轮询查询状态）
     */
    @PostMapping("/ai-draft/tasks")
    public ResponseEntity<AiDraftTaskStartResponse> startAiDraftTask(@PathVariable Long shopId,
                                                                     @RequestBody(required = false) AiDraftRequest request,
                                                                     Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(aiDraftTaskService.start(userId, shopId, request));
    }

    /**
     * 查询 AI 草稿生成任务状态。
     *
     * <p>路径：GET /api/shops/{shopId}/reviews/ai-draft/tasks/{taskId}
     */
    @GetMapping("/ai-draft/tasks/{taskId}")
    public ResponseEntity<AiDraftTaskStatusResponse> getAiDraftTask(@PathVariable Long shopId,
                                                                    @PathVariable String taskId,
                                                                    Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(aiDraftTaskService.getStatus(userId, shopId, taskId));
    }

    /**
     * AI 点评推荐：基于用户偏好推荐店铺的热门点评。
     *
     * <p>路径：GET /api/shops/{shopId}/reviews/recommend
     * <p>认证：可匿名；有登录态时可带 userId 做后续个性化扩展。
     * <p>入参：preference（偏好关键词，可选），limit（数量，可选，默认 3）
     * <p>出参：点评+推荐理由列表
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<AiRecommendItemResponse>> recommend(@PathVariable Long shopId,
                                                                   @RequestParam(required = false) String preference,
                                                                   @RequestParam(required = false, defaultValue = "3") Integer limit,
                                                                   Authentication authentication) {
        Long userId = authentication == null ? null : Long.parseLong(authentication.getName());
        int safeLimit = limit == null ? 3 : limit;
        List<AiRecommendItemResponse> result = aiReviewService.recommendReviews(userId, shopId, preference, safeLimit);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable Long shopId,
                                    @PathVariable Long reviewId,
                                    Authentication authentication) {
        if (!hasPermissionForShop(shopId, authentication)) {
            return ResponseEntity.status(403).body(Map.of("code", 403, "message", "无权限操作该店铺点评"));
        }
        Review review = reviewService.getById(reviewId);
        if (review == null || review.getShopId() == null || !review.getShopId().equals(shopId)) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "点评不存在"));
        }
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

