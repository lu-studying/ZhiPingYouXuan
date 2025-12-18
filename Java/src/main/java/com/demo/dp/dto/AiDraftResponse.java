package com.demo.dp.dto;

/**
 * AI 点评草稿生成响应 DTO。
 *
 * <p>封装生成结果，便于前端显示与编辑。
 */
public class AiDraftResponse {

    /**
     * 生成的点评草稿文本。
     */
    private String draft;

    public AiDraftResponse() {}

    public AiDraftResponse(String draft) {
        this.draft = draft;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }
}

