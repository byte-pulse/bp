package cloud.bytepulse.common.core.constant;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 16:46
 */
public class ApiPathRegistry {

    /**
     * 无日志接口
     */
    public final static Map<String, Set<String>> NOLOGGING_API = new ConcurrentHashMap<>();

    /**
     * 匿名接口
     */
    public final static Map<String, Set<String>> ANONYMOUS_API = new ConcurrentHashMap<>();

    static {
        Set<String> defaultUrls = Set.of(
                "/favicon.ico",
                "/swagger-ui.html",
                "/swagger-ui/*",
                "/swagger-resources/**",
                "/v2/api-docs",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/doc.html",
                "/META-INF/resources/webjars/**",
                "/druid/**",
                "/actuator/**",
                "/error"
        );
        for (RequestMethod method : RequestMethod.values()) {
            String methodName = method.name();
            ANONYMOUS_API.computeIfAbsent(methodName, k -> ConcurrentHashMap.newKeySet())
                    .addAll(defaultUrls);
            NOLOGGING_API.computeIfAbsent(methodName, k -> ConcurrentHashMap.newKeySet())
                    .addAll(defaultUrls);
        }
    }
}
