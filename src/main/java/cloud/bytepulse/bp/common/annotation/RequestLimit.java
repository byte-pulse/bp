package cloud.bytepulse.bp.common.annotation;

import java.lang.annotation.*;

/**
 * 接口请求限制
 *
 * @author jiejiebiezheyang
 * @since 2024-05-07 14:00
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
