package com.demo.dp.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * LLM 客户端：支持配置化调用，含超时与简单降级。
 *
 * <p>配置项（application.yml）：
 * <ul>
 *     <li>ai.provider: mock / openai / qwen 等</li>
 *     <li>ai.endpoint: 大模型 HTTP 接口地址（如 OpenAI 或通义千问兼容模式）</li>
 *     <li>ai.api-key: API Key（也可以留空，使用环境变量）</li>
 *     <li>ai.model: 模型名称，如 gpt-3.5-turbo 或 qwen3-max</li>
 *     <li>ai.timeout-ms: 8000</li>
 * </ul>
 *
 * <p>通义千问接入说明（你当前的需求）：
 * <ul>
 *     <li>系统环境变量：DASHSCOPE_API_KEY（你已经配置好了）</li>
 *     <li>application.yml：
 *       <pre>
 *       ai:
 *         provider: qwen
 *         endpoint: "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
 *         api-key: ""          # 保持为空，从 DASHSCOPE_API_KEY 读取
 *         model: "qwen3-max"
 *         timeout-ms: 8000
 *       </pre>
 *     </li>
 * </ul>
 *
 * <p>兼容模式说明：通义千问的 compatible-mode 接口与 OpenAI chat/completions 协议基本一致，
 * 因此这里直接复用同一套请求格式，只是 endpoint 和 model 不同。
 */
@Component
public class AiClient {

    private static final Logger log = LoggerFactory.getLogger(AiClient.class);

    private final RestTemplate restTemplate;

    @Value("${ai.provider:mock}")
    private String provider;   // mock / openai / qwen 等

    @Value("${ai.endpoint:https://api.openai.com/v1/chat/completions}")
    private String endpoint;   // 模型 HTTP 接口地址

    @Value("${ai.api-key:}")
    private String apiKey;     // 优先使用配置文件中的 key，对通义我们会从环境变量读取

    @Value("${ai.model:gpt-3.5-turbo}")
    private String model;      // 模型名称：如 gpt-3.5-turbo / qwen3-max

    @Value("${ai.timeout-ms:8000}")
    private int timeoutMs;     // 读取超时时间（毫秒），目前用于构造 RestTemplate

    public AiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(8000))
                .build();
    }

    /**
     * 调用大模型生成。
     *
     * @param prompt 拼接好的 prompt
     * @return 大模型输出（或 mock）
     */
    public String callLlm(String prompt) {
        // 统一解析 API Key（支持从环境变量读取通义千问的 DASHSCOPE_API_KEY）
        String resolvedKey = resolveApiKey();

        // 若未配置真实 Key 或 provider=mock，则走本地 mock，避免启动时报错
        if (resolvedKey == null || resolvedKey.isBlank() || "mock".equalsIgnoreCase(provider)) {
            return "【mock响应】" + prompt;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 兼容模式下，通义千问也是标准的 Bearer 头；如果后续需要 X-DashScope-API-Key，可在此处调整
            headers.setBearerAuth(resolvedKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            // 简单 chat/completions 请求格式
            body.put("messages", java.util.List.of(
                    Map.of("role", "user", "content", prompt)
            ));
            body.put("temperature", 0.7);
            body.put("max_tokens", 200);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            Map<?, ?> resp = restTemplate.postForObject(endpoint, entity, Map.class);
            // 解析简单返回
            Object choices = resp == null ? null : resp.get("choices");
            if (choices instanceof java.util.List && !((java.util.List<?>) choices).isEmpty()) {
                Object first = ((java.util.List<?>) choices).get(0);
                if (first instanceof Map) {
                    Object msg = ((Map<?, ?>) first).get("message");
                    if (msg instanceof Map) {
                        Object content = ((Map<?, ?>) msg).get("content");
                        if (content != null) {
                            return content.toString();
                        }
                    }
                }
            }
            // 未解析到内容时兜底
            return "生成失败，请稍后重试";
        } catch (Exception e) {
            log.error("LLM 调用失败", e);
            return "生成失败，请稍后重试";
        }
    }

    /**
     * 统一解析 API Key 的逻辑：
     * <ul>
     *     <li>优先使用 application.yml 中配置的 ai.api-key（如果不为空）</li>
     *     <li>当 provider=qwen 时，若 ai.api-key 为空，则从环境变量 DASHSCOPE_API_KEY 读取</li>
     *     <li>其他 provider 可按需扩展</li>
     * </ul>
     *
     * @return 实际用于调用的 API Key，可能为 null
     */
    private String resolveApiKey() {
        // 1. 如果在 application.yml 中显式配置了 ai.api-key，则直接使用
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey;
        }

        // 2. 对于通义千问（provider=qwen），优先从环境变量 DASHSCOPE_API_KEY 读取
        if ("qwen".equalsIgnoreCase(provider)) {
            String envKey = System.getenv("DASHSCOPE_API_KEY");
            if (envKey != null && !envKey.isBlank()) {
                return envKey;
            }
        }

        // 3. 其他情况暂不做特殊处理，返回配置中的 apiKey（此时为空）
        return apiKey;
    }
}

