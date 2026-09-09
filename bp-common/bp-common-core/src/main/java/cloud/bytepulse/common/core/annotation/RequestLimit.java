package cloud.bytepulse.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:57
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {
    /**
     * 时间窗口，单位毫秒
     * 默认1分钟
     */
    long time() default 60000;

    /**
     * 请求次数
     */
    int count() default 5;
}
