package cloud.bytepulse.common.core.annotation;

import cloud.bytepulse.common.core.enums.OperateEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录文件
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:55
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BpLogging {

    OperateEnum value() default OperateEnum.QUERY;

    String desc() default "";
}
