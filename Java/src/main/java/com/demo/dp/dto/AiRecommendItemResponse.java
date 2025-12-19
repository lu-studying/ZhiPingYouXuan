package com.demo.dp.dto;

import com.demo.dp.domain.entity.Review;

/**
 * AI 点评推荐返回项：包含点评本身以及推荐理由。
 */
public class AiRecommendItemResponse {

    /**
     * 被推荐的点评。
     */
    private Review review;

    /**
     * 推荐理由（可读字符串），例如：
     * "因为你是爱吃辣用户，这家店有“辣”标签，这条点评里提到了“辣”。"
     */
    private String reason;

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}


