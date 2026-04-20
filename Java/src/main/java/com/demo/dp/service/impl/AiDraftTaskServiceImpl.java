package com.demo.dp.service.impl;

import com.demo.dp.dto.AiDraftRequest;
import com.demo.dp.dto.AiDraftTaskStartResponse;
import com.demo.dp.dto.AiDraftTaskStatusResponse;
import com.demo.dp.service.AiDraftTaskService;
import com.demo.dp.service.AiReviewService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class AiDraftTaskServiceImpl implements AiDraftTaskService {

    private static final Duration TTL = Duration.ofMinutes(10);

    private final AiReviewService aiReviewService;
    private final ExecutorService executor;
    private final ConcurrentHashMap<String, TaskRecord> tasks = new ConcurrentHashMap<>();

    public AiDraftTaskServiceImpl(AiReviewService aiReviewService) {
        this.aiReviewService = aiReviewService;
        this.executor = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));
        startCleanupThread();
    }

    @Override
    public AiDraftTaskStartResponse start(Long userId, Long shopId, AiDraftRequest request) {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();

        TaskRecord record = new TaskRecord(userId, shopId, now);
        record.status = "queued";
        tasks.put(taskId, record);

        CompletableFuture
                .supplyAsync(() -> {
                    record.status = "running";
                    String preference = request == null ? null : request.getPreference();
                    return aiReviewService.generateDraft(userId, shopId, preference);
                }, executor)
                .whenComplete((draft, ex) -> {
                    record.finishedAtMs = System.currentTimeMillis();
                    if (ex != null) {
                        record.status = "failed";
                        record.error = unwrap(ex);
                    } else {
                        record.status = "succeeded";
                        record.draft = draft;
                    }
                });

        return new AiDraftTaskStartResponse(taskId);
    }

    @Override
    public AiDraftTaskStatusResponse getStatus(Long userId, Long shopId, String taskId) {
        TaskRecord record = tasks.get(taskId);
        if (record == null) {
            return new AiDraftTaskStatusResponse("not_found", null, "任务不存在或已过期");
        }
        if (!safeEq(record.userId, userId) || !safeEq(record.shopId, shopId)) {
            return new AiDraftTaskStatusResponse("forbidden", null, "无权限访问该任务");
        }
        return new AiDraftTaskStatusResponse(record.status, record.draft, record.error);
    }

    private void startCleanupThread() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    long now = System.currentTimeMillis();
                    for (var entry : tasks.entrySet()) {
                        TaskRecord r = entry.getValue();
                        long ts = r.finishedAtMs > 0 ? r.finishedAtMs : r.createdAtMs;
                        if (now - ts > TTL.toMillis()) {
                            tasks.remove(entry.getKey());
                        }
                    }
                    Thread.sleep(30_000);
                } catch (InterruptedException ignored) {
                    // continue
                } catch (Exception ignored) {
                    // continue
                }
            }
        }, "ai-draft-task-cleaner");
        t.setDaemon(true);
        t.start();
    }

    private static boolean safeEq(Long a, Long b) {
        return a != null && a.equals(b);
    }

    private static String unwrap(Throwable ex) {
        Throwable t = ex;
        while (t instanceof CompletionException || t instanceof ExecutionException) {
            if (t.getCause() == null) break;
            t = t.getCause();
        }
        String msg = t.getMessage();
        return msg == null || msg.isBlank() ? t.getClass().getSimpleName() : msg;
    }

    private static class TaskRecord {
        final Long userId;
        final Long shopId;
        final long createdAtMs;
        volatile long finishedAtMs;
        volatile String status;
        volatile String draft;
        volatile String error;

        TaskRecord(Long userId, Long shopId, long createdAtMs) {
            this.userId = userId;
            this.shopId = shopId;
            this.createdAtMs = createdAtMs;
        }
    }
}

