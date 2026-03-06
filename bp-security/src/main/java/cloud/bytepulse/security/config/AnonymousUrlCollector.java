package cloud.bytepulse.security.config;

import cloud.bytepulse.common.core.annotation.Anonymous;
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
 * @author jiejiebiezheyang
 * @since 2026-09-08 18:15
 */
@Slf4j
@Component
public class AnonymousUrlCollector {


    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public AnonymousUrlCollector(@Qualifier("requestMappingHandlerMapping")
                                 RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @PostConstruct
    public void init() {
        collectAnonymousUrls();
        int totalSize = ApiPathRegistry.ANONYMOUS_API.values()
                .stream()
                .mapToInt(Set::size)
                .sum();
        log.info("已注册 {} 个匿名接口", totalSize);
    }
}
