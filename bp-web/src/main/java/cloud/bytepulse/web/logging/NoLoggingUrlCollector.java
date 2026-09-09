package cloud.bytepulse.web.logging;

import cloud.bytepulse.common.core.annotation.NoLogging;
import cloud.bytepulse.common.core.constant.ApiPathRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 无需记录日志的接口收集
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 18:15
 */
@Slf4j
@Component
public class NoLoggingUrlCollector {


    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public NoLoggingUrlCollector(@Qualifier("requestMappingHandlerMapping")
                                 RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @PostConstruct
    public void init() {
        collectAnonymousUrls();
        int totalSize = ApiPathRegistry.NOLOGGING_API.values()
                .stream()
                .mapToInt(Set::size)
                .sum();
        log.info("已忽略 {} 个接口日志", totalSize);
    }

    private void collectAnonymousUrls() {
        // 获取所有 RequestMapping 信息
        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
                requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo info = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            // 检查方法本身是否有 @NoLogging 注解
            boolean hasMethodAnnotation = handlerMethod.hasMethodAnnotation(NoLogging.class);

            // 检查方法所在的类是否有 @NoLogging 注解
            boolean hasClassAnnotation = handlerMethod.getBeanType()
                    .isAnnotationPresent(NoLogging.class);

            if (hasMethodAnnotation || hasClassAnnotation) {
                PathPatternsRequestCondition condition = info.getPathPatternsCondition();
                if (condition == null) continue;
                Set<String> patternValues = condition.getPatternValues();
                Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
                // 如果没有指定请求方法, 默认支持所有请求方法
                if (methods.isEmpty()) {
                    for (RequestMethod method : RequestMethod.values()) {
                        ApiPathRegistry.NOLOGGING_API
                                .computeIfAbsent(
                                        method.name(),
                                        k -> ConcurrentHashMap.newKeySet()
                                )
                                .addAll(patternValues);
                    }
                } else {
                    // 按指定的请求方法添加 URL
                    for (RequestMethod method : methods) {
                        ApiPathRegistry.NOLOGGING_API
                                .computeIfAbsent(
                                        method.name(),
                                        k -> ConcurrentHashMap.newKeySet()
                                )
                                .addAll(patternValues);
                    }
                }
            }
        }
    }
}
