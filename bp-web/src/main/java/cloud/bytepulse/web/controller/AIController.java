package cloud.bytepulse.web.controller;

import cloud.bytepulse.ai.deepseek.DeepseekService;
import cloud.bytepulse.common.core.annotation.Anonymous;
import cloud.bytepulse.common.core.annotation.RequestLimit;
import cloud.bytepulse.common.core.model.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @RequestLimit(count = 10, time = 8000)
    public ApiResponse<String> generate(@RequestParam String message) {
        String result = deepseekService.generate(message);
        return ApiResponse.successWithString(result);
    }

    @PostMapping(value = "/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
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


    // 用一个固定线程池处理推送，避免阻塞请求线程
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @GetMapping(value = "/sse/demo", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Anonymous
    public SseEmitter demo() {
        // 超时时间 60 秒，到时间没推完会自动断开
        SseEmitter emitter = new SseEmitter(60_000L);

        executor.submit(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    // 发送一条消息，前端 onmessage 会收到
                    emitter.send("第 " + (i + 1) + " 条消息，时间戳：" + System.currentTimeMillis());
                    Thread.sleep(1000);
                }
                // 正常结束，关闭连接
                emitter.complete();
            } catch (Exception e) {
                // 出错时关闭连接
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
