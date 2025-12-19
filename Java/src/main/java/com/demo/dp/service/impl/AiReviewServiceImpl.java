package com.demo.dp.service;

import com.demo.dp.ai.AiClient;
import com.demo.dp.ai.PromptBuilder;
import com.demo.dp.domain.entity.AiCallLog;
import com.demo.dp.domain.entity.OrderRecord;
import com.demo.dp.domain.entity.Review;
import com.demo.dp.mapper.AiCallLogMapper;
import com.demo.dp.mapper.OrderRecordMapper;
import com.demo.dp.mapper.ReviewKeywordMapper;
import com.demo.dp.mapper.ReviewMapper;
import com.demo.dp.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AI 点评服务实现类：实现AI相关的业务逻辑。
 * 
 * 目前为示例占位，后续可扩展：
 * - 拼接用户历史、商家特征、关键词上下文
 * - 记录调用日志与超时重试
 */
@Service
public class AiReviewServiceImpl implements AiReviewService {

    /**
     * 日志记录器，用于输出调用链路和错误信息。
     */
    private static final Logger log = LoggerFactory.getLogger(AiReviewServiceImpl.class);

    private final AiClient aiClient;
    private final PromptBuilder promptBuilder;
    private final ReviewMapper reviewMapper;
    private final OrderRecordMapper orderRecordMapper;
    private final AiCallLogMapper aiCallLogMapper;
    private final ReviewKeywordMapper reviewKeywordMapper;
    private final TagService tagService;

    /**
     * 构造函数：注入大模型客户端、Prompt 组装器、点评查询、订单查询、AI 调用日志 Mapper。
     *
     * @param aiClient          大模型客户端
     * @param promptBuilder     Prompt 组装器
     * @param reviewMapper      点评数据访问层
     * @param orderRecordMapper 消费记录数据访问层，用于提供生成上下文
     * @param aiCallLogMapper   AI 调用日志记录 Mapper
     */
    public AiReviewServiceImpl(AiClient aiClient,
                               PromptBuilder promptBuilder,
                               ReviewMapper reviewMapper,
                               OrderRecordMapper orderRecordMapper,
                               AiCallLogMapper aiCallLogMapper,
                               ReviewKeywordMapper reviewKeywordMapper,
                               TagService tagService) {
        this.aiClient = aiClient;
        this.promptBuilder = promptBuilder;
        this.reviewMapper = reviewMapper;
        this.orderRecordMapper = orderRecordMapper;
        this.aiCallLogMapper = aiCallLogMapper;
        this.reviewKeywordMapper = reviewKeywordMapper;
        this.tagService = tagService;
    }

    /**
     * 生成点评草稿示例。
     */
    @Override
    public String generateDraft(Long userId, Long shopId, String context) {
        long start = System.currentTimeMillis();
        // 构造 Prompt：带上用户/商家标签、最近消费记录作为上下文
        OrderRecord latestOrder = orderRecordMapper.findLatestByUserAndShop(userId, shopId);
        java.util.List<com.demo.dp.domain.entity.Tag> userTags = tagService.listTagsOfUser(userId);
        java.util.List<com.demo.dp.domain.entity.Tag> shopTags = tagService.listTagsOfShop(shopId);
        String prompt = promptBuilder.buildGeneratePrompt(userId, shopId, context, latestOrder, userTags, shopTags);

        String response = null;
        int status = 1;
        String errorMsg = null;
        try {
            response = aiClient.callLlm(prompt);
            return response;
        } catch (Exception e) {
            status = 0;
            errorMsg = e.getMessage();
            log.error("AI generateDraft failed, userId={}, shopId={}", userId, shopId, e);
            throw e;
        } finally {
            // 记录调用日志（成功/失败都记）
            AiCallLog logRow = new AiCallLog();
            logRow.setUserId(userId);
            logRow.setType("generate");
            logRow.setPrompt(prompt);
            // 仅记录摘要，避免大文本；失败时记录错误信息
            logRow.setResponseRef(response == null ? errorMsg : abbreviate(response, 500));
            logRow.setLatencyMs((int) (System.currentTimeMillis() - start));
            logRow.setStatus(status);
            aiCallLogMapper.insert(logRow);
        }
    }

    /**
     * 基于用户偏好推荐点评：优先按关键词匹配，再按点赞/评分/时间排序；无匹配时兜底返回热门点评。
     */
    @Override
    public java.util.List<com.demo.dp.dto.AiRecommendItemResponse> recommendReviews(Long userId, Long shopId, String preference, int limit) {
        // 入参兜底：避免 limit 非法或过大
        int safeLimit = Math.min(Math.max(limit, 1), 10); // 1~10 之间

        // 如果未显式传入偏好，则尝试根据用户标签与商家标签推断一个偏好关键词
        String effectivePreference = (preference == null || preference.isBlank())
                ? inferPreferenceFromTags(userId, shopId)
                : preference;

        java.util.List<Review> candidates;

        // 1) 关键词索引召回（review_keyword表）
        java.util.List<Review> keywordHit = recommendByKeywordIndex(shopId, effectivePreference, safeLimit);
        if (!keywordHit.isEmpty()) {
            candidates = keywordHit;
        } else {
            // 2) 正文 LIKE 匹配
            java.util.List<Review> textHit = reviewMapper.findTopByShopAndKeyword(shopId, effectivePreference, safeLimit);
            if (textHit != null && !textHit.isEmpty()) {
                candidates = textHit;
            } else {
                // 3) 兜底：热门点评
                candidates = reviewMapper.findTopByShop(shopId, safeLimit);
            }
        }

        // 基于标签/偏好关键词进行二次排序
        java.util.List<String> preferenceKeywords = buildPreferenceKeywords(userId, shopId, effectivePreference);
        java.util.List<Review> reranked = rerankByKeywords(candidates, preferenceKeywords);
        // 保证不超过 safeLimit 条
        if (reranked.size() > safeLimit) {
            reranked = reranked.subList(0, safeLimit);
        }
        // 构造带推荐理由的返回结构
        return buildRecommendResponses(reranked, userId, shopId, preferenceKeywords);
    }

    /**
     * 使用关键词索引做召回。
     */
    private java.util.List<Review> recommendByKeywordIndex(Long shopId, String preference, int limit) {
        java.util.List<Review> result = new java.util.ArrayList<>();
        if (preference == null || preference.isBlank()) {
            return result;
        }
        java.util.List<Long> reviewIds = reviewKeywordMapper.findReviewIdsByShopAndKeyword(shopId, preference, limit);
        if (reviewIds == null || reviewIds.isEmpty()) {
            return result;
        }
        for (Long id : reviewIds) {
            Review r = reviewMapper.findById(id);
            if (r != null && r.getStatus() != null && r.getStatus() == 1) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * 截断长文本，避免日志过大。
     */
    private String abbreviate(String text, int maxLen) {
        if (text == null) {
            return null;
        }
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen) + "...";
    }

    /**
     * 根据用户标签和商家标签推断偏好关键词。
     *
     * <p>策略（MVP）：
     * <ul>
     *     <li>取当前用户的 user 标签名称列表（如：爱吃辣、口味清淡、环境控）</li>
     *     <li>取当前商家的 shop 标签名称列表（如：火锅、适合聚会、辣、清淡）</li>
     *     <li>从一个关键词词典中，查找同时出现在用户标签或商家标签中的词，作为偏好</li>
     *     <li>若找到第一个匹配（如“辣”、“清淡”、“环境”等），返回该词；否则返回 null</li>
     * </ul>
     */
    private String inferPreferenceFromTags(Long userId, Long shopId) {
        if (userId == null) {
            return null;
        }
        java.util.List<com.demo.dp.domain.entity.Tag> userTags = tagService.listTagsOfUser(userId);
        java.util.List<com.demo.dp.domain.entity.Tag> shopTags = tagService.listTagsOfShop(shopId);
        if ((userTags == null || userTags.isEmpty()) && (shopTags == null || shopTags.isEmpty())) {
            return null;
        }
        java.util.Set<String> names = new java.util.HashSet<>();
        if (userTags != null) {
            for (com.demo.dp.domain.entity.Tag t : userTags) {
                if (t.getName() != null) {
                    names.add(t.getName());
                }
            }
        }
        if (shopTags != null) {
            for (com.demo.dp.domain.entity.Tag t : shopTags) {
                if (t.getName() != null) {
                    names.add(t.getName());
                }
            }
        }
        if (names.isEmpty()) {
            return null;
        }
        // 关键词词典：可根据实际标签命名调整
        String[] dict = new String[]{
                "辣", "微辣", "不辣", "清淡",
                "环境", "服务", "价格", "性价比", "分量"
        };
        for (String kw : dict) {
            for (String n : names) {
                if (n.contains(kw)) {
                    return kw;
                }
            }
        }
        return null;
    }

    /**
     * 构建用于排序的偏好关键词列表：
     * <ul>
     *     <li>包含显式传入的 preference（如果有）；</li>
     *     <li>包含根据用户/商家标签推断出的关键词（如“辣”、“环境”等）；</li>
     * </ul>
     */
    private java.util.List<String> buildPreferenceKeywords(Long userId, Long shopId, String effectivePreference) {
        java.util.Set<String> set = new java.util.HashSet<>();
        if (effectivePreference != null && !effectivePreference.isBlank()) {
            set.add(effectivePreference);
        }
        // 再根据标签名称再扩展一些关键词（与 inferPreferenceFromTags 使用同一词典）
        java.util.List<com.demo.dp.domain.entity.Tag> userTags = userId == null ? java.util.List.of() : tagService.listTagsOfUser(userId);
        java.util.List<com.demo.dp.domain.entity.Tag> shopTags = tagService.listTagsOfShop(shopId);
        java.util.Set<String> names = new java.util.HashSet<>();
        for (com.demo.dp.domain.entity.Tag t : userTags) {
            if (t.getName() != null) {
                names.add(t.getName());
            }
        }
        for (com.demo.dp.domain.entity.Tag t : shopTags) {
            if (t.getName() != null) {
                names.add(t.getName());
            }
        }
        String[] dict = new String[]{
                "辣", "微辣", "不辣", "清淡",
                "环境", "服务", "价格", "性价比", "分量"
        };
        for (String kw : dict) {
            for (String n : names) {
                if (n.contains(kw)) {
                    set.add(kw);
                }
            }
        }
        return new java.util.ArrayList<>(set);
    }

    /**
     * 基于关键词对候选点评进行二次排序，体现个性化偏好。
     *
     * <p>简单打分策略（MVP）：
     * <ul>
     *     <li>基础分 = likeCount * 2 + rating</li>
     *     <li>如果点评内容命中某个偏好关键词，则额外增加一定权重（例如每个关键词 +5 分）</li>
     *     <li>按照得分从高到低排序</li>
     * </ul>
     */
    private java.util.List<Review> rerankByKeywords(java.util.List<Review> reviews, java.util.List<String> keywords) {
        if (reviews == null || reviews.isEmpty() || keywords == null || keywords.isEmpty()) {
            return reviews == null ? java.util.List.of() : reviews;
        }
        java.util.List<Review> copy = new java.util.ArrayList<>(reviews);
        copy.sort((a, b) -> {
            double scoreA = scoreReview(a, keywords);
            double scoreB = scoreReview(b, keywords);
            return Double.compare(scoreB, scoreA);
        });
        return copy;
    }

    /**
     * 计算单条点评的推荐分数。
     */
    private double scoreReview(Review r, java.util.List<String> keywords) {
        if (r == null) {
            return 0;
        }
        int like = r.getLikeCount() == null ? 0 : r.getLikeCount();
        int rating = r.getRating() == null ? 0 : r.getRating();
        double score = like * 2.0 + rating;
        String content = r.getContent() == null ? "" : r.getContent();
        for (String kw : keywords) {
            if (kw != null && !kw.isBlank() && content.contains(kw)) {
                score += 5.0; // 命中一个偏好关键词 +5 分
            }
        }
        return score;
    }

    /**
     * 将排序后的点评列表包装为带推荐理由的响应结构。
     */
    private java.util.List<com.demo.dp.dto.AiRecommendItemResponse> buildRecommendResponses(
            java.util.List<Review> reviews,
            Long userId,
            Long shopId,
            java.util.List<String> keywords) {
        java.util.List<com.demo.dp.dto.AiRecommendItemResponse> result = new java.util.ArrayList<>();
        if (reviews == null || reviews.isEmpty()) {
            return result;
        }
        java.util.List<com.demo.dp.domain.entity.Tag> userTags = userId == null ? java.util.List.of() : tagService.listTagsOfUser(userId);
        java.util.List<com.demo.dp.domain.entity.Tag> shopTags = tagService.listTagsOfShop(shopId);
        String userTagStr = buildTagString(userTags);
        String shopTagStr = buildTagString(shopTags);

        for (Review r : reviews) {
            com.demo.dp.dto.AiRecommendItemResponse item = new com.demo.dp.dto.AiRecommendItemResponse();
            item.setReview(r);
            item.setReason(buildReasonForReview(r, userTagStr, shopTagStr, keywords));
            result.add(item);
        }
        return result;
    }

    private String buildTagString(java.util.List<com.demo.dp.domain.entity.Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (com.demo.dp.domain.entity.Tag t : tags) {
            if (t.getName() != null && !t.getName().isBlank()) {
                if (!sb.isEmpty()) {
                    sb.append("、");
                }
                sb.append(t.getName());
            }
        }
        return sb.toString();
    }

    private String buildReasonForReview(Review review,
                                        String userTagStr,
                                        String shopTagStr,
                                        java.util.List<String> keywords) {
        String content = review.getContent() == null ? "" : review.getContent();
        String hitKeyword = null;
        if (keywords != null) {
            for (String kw : keywords) {
                if (kw != null && !kw.isBlank() && content.contains(kw)) {
                    hitKeyword = kw;
                    break;
                }
            }
        }
        StringBuilder reason = new StringBuilder();
        if (userTagStr != null && !userTagStr.isBlank()) {
            reason.append("因为你是「").append(userTagStr).append("」类型的用户；");
        }
        if (shopTagStr != null && !shopTagStr.isBlank()) {
            reason.append("这家店被标记为：").append(shopTagStr).append("；");
        }
        if (hitKeyword != null) {
            reason.append("这条点评里提到了「").append(hitKeyword).append("」。");
        }
        if (reason.isEmpty()) {
            reason.append("根据该店的代表性热门点评为你推荐。");
        }
        return reason.toString();
    }
}

