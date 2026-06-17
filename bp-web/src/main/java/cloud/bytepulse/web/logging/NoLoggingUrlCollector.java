package cloud.bytepulse.web.logging;

import cloud.bytepulse.common.core.annotation.BpLogging;
import cloud.bytepulse.common.core.constant.ApiPathRegistry;
import cloud.bytepulse.common.core.enums.OperateEnum;
import cloud.bytepulse.common.core.model.ApiLoggingInfo;
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
import java.util.stream.Collectors;

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
        int totalSize = ApiPathRegistry.LOGGING_API.values()
                .stream()
                .mapToInt(Set::size)
                .sum();
        log.info("已忽略 {} 个接口日志", totalSize);
    }
}
