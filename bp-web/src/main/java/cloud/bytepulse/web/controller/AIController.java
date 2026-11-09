package cloud.bytepulse.web.controller;

import cloud.bytepulse.ai.deepseek.DeepseekService;
import cloud.bytepulse.common.core.annotation.RequestLimit;
import cloud.bytepulse.common.core.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author jiejiebiezheyang
 * @since 2026-11-09 15:40
 */
@Slf4j
@Tag(name = "AI")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final DeepseekService deepseekService;


    @PostMapping("/generate")
    @Operation(summary = "对话")
    @RequestLimit(count = 10, time = 8000)
    public ApiResponse<String> generate(@RequestParam String message) {
        String result = deepseekService.generate(message);
        return ApiResponse.successWithString(result);
    }

    @PostMapping("/generateStream")
    @Operation(summary = "流式对话")
    @RequestLimit(count = 10, time = 8000)
    public SseEmitter generateStream(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(60_000L);
        Flux<String> flux = deepseekService.generateStream(message);

        Disposable subscription = flux.subscribe(
                chunk -> {
                    try {
                        emitter.send(SseEmitter.event().name("message").data(chunk));
                    } catch (IOException e) {
                        // 客户端断开或写入失败, 仅取消订阅, 不调用 completeWithError
                        // 否则会触发 Tomcat ERROR 派发到 /error, 导致二次异常
                        log.debug("SSE 写入失败, 客户端可能已断开: {}", e.getMessage());
                    }
                },
                error -> {
                    log.error("流式对话失败", error);
                    try {
                        emitter.send(SseEmitter.event().name("error").data("AI 服务暂时不可用，请稍后重试"));
                    } catch (IOException ignored) {
                    }
                    emitter.complete();
                },
                emitter::complete
        );

        emitter.onCompletion(subscription::dispose);
        emitter.onTimeout(() -> {
            subscription.dispose();
            emitter.complete();
        });
        emitter.onError(e -> {
            subscription.dispose();
            log.warn("SSE emitter 错误", e);
        });

        return emitter;
    }
}
