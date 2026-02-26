package cloud.bytepulse.bp.common.annotation;

import java.lang.annotation.*;

/**
 * 允许匿名访问注解
 *
 * @author jiejiebiezheyang
 * @since 2024-03-01 15:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {
}
