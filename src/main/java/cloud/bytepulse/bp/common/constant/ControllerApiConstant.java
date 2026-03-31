package cloud.bytepulse.bp.common.constant;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 接口相关常量
 *
 * @author jiejiebiezheyang
 * @since 2026-03-26 15:35
 */
@Slf4j
public class ControllerApiConstant {

    /**
     * 所有接口
     */
    public final static Set<String> ALL_API = new HashSet<>();
    /**
     * 匿名接口
     */
    public final static Set<String> ANONYMOUS_API = new HashSet<>();
    /**
     * 外部接口
     */
    public final static Set<String> EXTERNAL_API = new HashSet<>();
    /**
     * 需要记录日志的接口
     */
    public final static Set<String> NEED_LOGGING_API = new HashSet<>();

    /**
     * 需要手动添加的匿名接口
     *
     */
    public static final Set<String> MANUAL_ANONYMOUS = Set.of(
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

    static {
        ALL_API.addAll(MANUAL_ANONYMOUS);
        ANONYMOUS_API.addAll(MANUAL_ANONYMOUS);
    }
}
