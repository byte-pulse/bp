package cloud.bytepulse.bp.common.utils;

import cloud.bytepulse.bp.framework.exception.BytePulseArgumentNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

/**
 * @author jiejiebiezheyang
 * @since 2025-07-26 17:00
 */
public class ValidUtils {

    private static final Validator validator;

    // 初始化静态 Validator（通过 Spring 或手动创建）
    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    /**
     * 手动触发数据分组校验
     */
    public static <T, I> void doValidate(T entity, Class<I> group) {
        Set<ConstraintViolation<T>> violations =
                validator.validate(entity, group);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .toList()
                    .toString();
            throw new BytePulseArgumentNotValidException(msg);
        }
    }
}