package com.demo.dp.service;

import com.demo.dp.domain.entity.Review;
import com.demo.dp.domain.entity.ReviewKeyword;
import com.demo.dp.mapper.ReviewKeywordMapper;
import com.demo.dp.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 点评服务实现类：实现点评相关的业务逻辑。
 * 
 * 实际场景还应包含：敏感词过滤、反垃圾、图片校验、审核状态处理等。
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final ReviewKeywordMapper reviewKeywordMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper,
                             ReviewKeywordMapper reviewKeywordMapper) {
        this.reviewMapper = reviewMapper;
        this.reviewKeywordMapper = reviewKeywordMapper;
    }

    /**
     * 分页查询商家点评（简化版，返回 List）
     * 如需完整 Page 对象，可自行封装分页信息
     */
    @Override
    public List<Review> listByShop(Long shopId, int page, int size) {
        int offset = page * size;
        return reviewMapper.findByShopId(shopId, offset, size);
    }

    /**
     * 获取商家点评总数
     */
    @Override
    public long countByShopId(Long shopId) {
        return reviewMapper.countByShopId(shopId);
    }

    @Override
    @Transactional
    public Review createReview(Long userId, Long shopId, Integer rating, String content, String images) {
        Review r = new Review();
        r.setUserId(userId);
        r.setShopId(shopId);
        r.setRating(rating);
        r.setContent(content);
        r.setImages(images);
        r.setIsAiGenerated(false);
        r.setLikeCount(0);
        r.setStatus(1);
        r.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(r);

        // 简单关键词抽取并入库（用于推荐召回）
        List<ReviewKeyword> keywords = extractKeywords(r.getId(), content);
        if (!keywords.isEmpty()) {
            reviewKeywordMapper.insertBatch(keywords);
        }
        return r;
    }

    /**
     * 点赞点评：当前实现为简单的 like_count 自增，未做防重复点赞。
     *
     * @param userId   点赞用户ID
     * @param reviewId 点评ID
     */
    @Override
    @Transactional
    public void likeReview(Long userId, Long reviewId) {
        // 检查点评是否存在且为正常状态
        Review review = reviewMapper.findById(reviewId);
        if (review == null || review.getStatus() == null || review.getStatus() != 1) {
            throw new RuntimeException("点评不存在或已下线，ID: " + reviewId);
        }
        // TODO: 可在此处增加 userId + reviewId 维度的防重复点赞逻辑（新增点赞表），当前为简单自增
        int rows = reviewMapper.increaseLikeCount(reviewId);
        if (rows == 0) {
            throw new RuntimeException("点赞失败，点评ID: " + reviewId);
        }
    }

    /**
     * 关键词抽取：基于规则的简单命中，用于 MVP。
     * 可后续替换为 NLP/LLM 标注。
     */
    private List<ReviewKeyword> extractKeywords(Long reviewId, String content) {
        List<ReviewKeyword> list = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return list;
        }
        String c = content.toLowerCase();
        // 简单规则：包含关键词则打标签
        Set<String> dict = Set.of(
                "辣", "微辣", "不辣", "麻", "清淡",
                "环境", "干净", "卫生",
                "服务", "态度",
                "排队", "等位",
                "性价比", "价格", "贵", "便宜",
                "分量", "份量"
        );
        for (String k : dict) {
            if (c.contains(k)) {
                ReviewKeyword kw = new ReviewKeyword();
                kw.setReviewId(reviewId);
                kw.setKeyword(k);
                kw.setWeight(1.0);
                list.add(kw);
            }
        }
        return list;
    }
}

