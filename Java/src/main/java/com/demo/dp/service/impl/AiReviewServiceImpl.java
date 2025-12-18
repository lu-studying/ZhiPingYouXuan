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
                               ReviewKeywordMapper reviewKeywordMapper) {
        this.aiClient = aiClient;
        this.promptBuilder = promptBuilder;
        this.reviewMapper = reviewMapper;
        this.orderRecordMapper = orderRecordMapper;
        this.aiCallLogMapper = aiCallLogMapper;
        this.reviewKeywordMapper = reviewKeywordMapper;
    }

    /**
     * 生成点评草稿示例。
     */
    @Override
    public String generateDraft(Long userId, Long shopId, String context) {
        long start = System.currentTimeMillis();
        // 构造 Prompt：带上用户/商家信息、最近消费记录作为上下文
        OrderRecord latestOrder = orderRecordMapper.findLatestByUserAndShop(userId, shopId);
        String prompt = promptBuilder.buildGeneratePrompt(userId, shopId, context, latestOrder);

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
    public java.util.List<Review> recommendReviews(Long userId, Long shopId, String preference, int limit) {
        // 入参兜底：避免 limit 非法或过大
        int safeLimit = Math.min(Math.max(limit, 1), 10); // 1~10 之间

        // 1) 关键词索引召回（review_keyword表）
        java.util.List<Review> keywordHit = recommendByKeywordIndex(shopId, preference, safeLimit);
        if (!keywordHit.isEmpty()) {
            return keywordHit;
        }
        // 2) 正文 LIKE 匹配
        java.util.List<Review> textHit = reviewMapper.findTopByShopAndKeyword(shopId, preference, safeLimit);
        if (textHit != null && !textHit.isEmpty()) {
            return textHit;
        }
        // 3) 兜底：热门点评
        return reviewMapper.findTopByShop(shopId, safeLimit);
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
}

