package com.demo.dp.service;

/**
 * AI 点评服务接口：定义AI相关的业务操作。
 * 
 * 主要功能：
 * - 生成点评草稿
 * - 推荐排序（后续扩展）
 */
public interface AiReviewService {

    /**
     * 生成点评草稿
     * 
     * @param userId 用户ID
     * @param shopId 商家ID
     * @param context 上下文信息（如用户历史消费记录、商家特征等）
     * @return 生成的点评草稿内容
     */
    String generateDraft(Long userId, Long shopId, String context);

    /**
     * 基于用户偏好为商家推荐点评列表。
     *
     * @param userId     用户ID（可空，匿名推荐传 null）
     * @param shopId     商家ID
     * @param preference 用户偏好关键词（如“辣”“微辣”“不辣”“环境”），可空
     * @param limit      返回数量上限
     * @return 推荐点评列表（包含点评与推荐理由），已按匹配度/点赞/时间排序
     */
    java.util.List<com.demo.dp.dto.AiRecommendItemResponse> recommendReviews(Long userId, Long shopId, String preference, int limit);
}
