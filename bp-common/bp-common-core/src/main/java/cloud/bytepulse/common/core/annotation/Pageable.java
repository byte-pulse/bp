package cloud.bytepulse.common.core.annotation;

import java.lang.annotation.*;

/**
 * 用来告诉 springdoc 这个接口需要分页
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 12:09
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pageable {
}
