package com.demo.dp.controller;

import com.demo.dp.domain.entity.Review;
import com.demo.dp.dto.AiDraftRequest;
import com.demo.dp.dto.AiDraftResponse;
import com.demo.dp.dto.AiRecommendRequest;
import com.demo.dp.dto.ReviewCreateRequest;
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
 * 后续可增加排序、过滤、AI 推荐等参数。
 */
@RestController
@RequestMapping("/api/shops/{shopId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final AiReviewService aiReviewService;

    /**
     * 构造函数：注入点评服务和 AI 点评服务。
     *
     * @param reviewService   点评业务服务
     * @param aiReviewService AI 点评业务服务
     */
    public ReviewController(ReviewService reviewService, AiReviewService aiReviewService) {
        this.reviewService = reviewService;
        this.aiReviewService = aiReviewService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@PathVariable Long shopId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        List<Review> reviews = reviewService.listByShop(shopId, page, size);
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
     * AI 点评推荐：基于用户偏好推荐店铺的热门点评。
     *
     * <p>路径：GET /api/shops/{shopId}/reviews/recommend
     * <p>认证：可匿名；有登录态时可带 userId 做后续个性化扩展。
     * <p>入参：preference（偏好关键词，可选），limit（数量，可选，默认 3）
     * <p>出参：点评列表
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<Review>> recommend(@PathVariable Long shopId,
                                                  @RequestParam(required = false) String preference,
                                                  @RequestParam(required = false, defaultValue = "3") Integer limit,
                                                  Authentication authentication) {
        Long userId = authentication == null ? null : Long.parseLong(authentication.getName());
        int safeLimit = limit == null ? 3 : limit;
        List<Review> result = aiReviewService.recommendReviews(userId, shopId, preference, safeLimit);
        return ResponseEntity.ok(result);
    }
}

