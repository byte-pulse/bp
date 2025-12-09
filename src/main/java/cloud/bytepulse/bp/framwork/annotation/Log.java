package cloud.bytepulse.bp.framwork.annotation;

import java.lang.annotation.*;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-11 14:00
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {
    /**
     * 接口名称
     */
    String value();

    /**
     * 是否持久化
     */
    boolean persist() default false;
}
