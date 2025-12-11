package cloud.bytepulse.bp.framwork.annotation;

import java.lang.annotation.*;

/**
 * 无日志接口
 *
 * @author jiejiebiezheyang
 * @since 2025-12-11 17:28
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoLogging {
}
