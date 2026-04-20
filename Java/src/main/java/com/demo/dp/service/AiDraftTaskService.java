package com.demo.dp.service;

import com.demo.dp.dto.AiDraftRequest;
import com.demo.dp.dto.AiDraftTaskStartResponse;
import com.demo.dp.dto.AiDraftTaskStatusResponse;

/**
 * AI 草稿生成异步任务服务。
 */
public interface AiDraftTaskService {
    AiDraftTaskStartResponse start(Long userId, Long shopId, AiDraftRequest request);

    AiDraftTaskStatusResponse getStatus(Long userId, Long shopId, String taskId);
}

