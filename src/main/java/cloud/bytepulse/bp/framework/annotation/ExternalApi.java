package cloud.bytepulse.bp.framework.annotation;

import java.lang.annotation.*;

/**
 * 第三方接口注解
 *
 * @author jiejiebiezheyang
 * @since 2026-01-20 10:57
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExternalApi {
}
