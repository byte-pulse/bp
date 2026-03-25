package cloud.bytepulse.bp.common.annotation;

import java.lang.annotation.*;

/**
 * 用来告诉 springdoc 这个接口需要分页
 *
 * @author jiejiebiezheyang
 * @since 2026-03-25 17:55
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pageable {
}
