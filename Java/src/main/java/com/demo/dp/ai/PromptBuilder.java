package com.demo.dp.ai;

import org.springframework.stereotype.Component;
import com.demo.dp.domain.entity.OrderRecord;
import com.demo.dp.domain.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Prompt 组装器，占位示例。
 * 可以扩展：
 * - 注入模板
 * - 控制上下文截断
 * - 多语言支持
 */
@Component
public class PromptBuilder {

    /**
     * 拼装点评生成 Prompt：包含用户、商家、偏好，以及最近一次消费记录（若有）。
     *
     * @param userId      用户 ID
     * @param shopId      商家 ID
     * @param preference  用户的口味/风格偏好描述，可为空（来自前端输入）
     * @param latestOrder 用户在该店最近一次消费记录，可为空
     * @param userTags    用户标签列表（如：爱吃辣、环境控），可为空
     * @param shopTags    商家标签列表（如：火锅、适合聚会），可为空
     * @return 拼装好的 Prompt 文本
     */
    public String buildGeneratePrompt(Long userId,
                                      Long shopId,
                                      String preference,
                                      OrderRecord latestOrder,
                                      List<Tag> userTags,
                                      List<Tag> shopTags) {
        String orderContext = latestOrder == null ? "无历史消费记录"
                : "最近一次消费：金额 " + latestOrder.getAmount() + "，到店时间 " + latestOrder.getVisitTime()
                + "，消费项 " + latestOrder.getItems();

        String userTagStr = (userTags == null || userTags.isEmpty())
                ? "无"
                : userTags.stream()
                          .map(Tag::getName)
                          .filter(n -> n != null && !n.isBlank())
                          .collect(Collectors.joining("、"));

        String shopTagStr = (shopTags == null || shopTags.isEmpty())
                ? "无"
                : shopTags.stream()
                          .map(Tag::getName)
                          .filter(n -> n != null && !n.isBlank())
                          .collect(Collectors.joining("、"));

        return """
                请基于以下信息生成一条用户点评：
                - 用户ID: %d
                - 商家ID: %d
                - 用户自由输入的偏好: %s
                - 用户标签画像: %s
                - 商家标签特征: %s
                - 历史消费: %s
                要求：结合上述信息，用中文生成一条真实、具体的用餐点评，50~120字，避免夸大或虚假表述。
                """.formatted(userId, shopId, preference, userTagStr, shopTagStr, orderContext);
    }
}

