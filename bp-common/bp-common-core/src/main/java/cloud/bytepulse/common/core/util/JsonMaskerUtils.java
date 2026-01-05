package cloud.bytepulse.common.core.util;

import dev.blaauwendraad.masker.json.JsonMasker;
import dev.blaauwendraad.masker.json.ValueMaskers;
import dev.blaauwendraad.masker.json.config.JsonMaskingConfig;
import dev.blaauwendraad.masker.json.config.KeyMaskingConfig;

import java.util.Collection;
import java.util.Set;

/**
 * JSON 脱敏工具
 *
 * @author jiejiebiezheyang
 * @since 2026-09-10 16:43
 */
public final class JsonMaskerUtils {

    /**
     * 默认需要脱敏的字段
     */
    private static final Set<String> DEFAULT_MASK_FIELDS = Set.of(
            "password",
            "pwd",
            "secret",
            "token",
            "accessToken",
            "refreshToken",
            "authorization"
    );

    private JsonMaskerUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 使用默认字段进行 JSON 脱敏
     *
     * @param jsonString JSON 字符串
     * @return 脱敏后的 JSON
     */
    public static String mask(String jsonString) {
        return mask(jsonString, DEFAULT_MASK_FIELDS);
    }

    /**
     * 自定义字段进行 JSON 脱敏
     *
     * @param jsonString JSON 字符串
     * @param fields     需要脱敏的字段
     * @return 脱敏后的 JSON
     */
    public static String mask(String jsonString, Collection<String> fields) {
        if (jsonString == null || jsonString.isBlank()) {
            return jsonString;
        }

        if (fields == null || fields.isEmpty()) {
            return jsonString;
        }

        JsonMasker masker = JsonMasker.getMasker(Set.copyOf(fields));

        return masker.mask(jsonString);
    }

    /**
     * 自定义字段进行 JSON 脱敏
     *
     * @param jsonString JSON 字符串
     * @param fields     需要脱敏的字段
     * @return 脱敏后的 JSON
     */
    public static String mask(String jsonString, String... fields) {
        if (fields == null || fields.length == 0) {
            return jsonString;
        }

        return mask(jsonString, Set.of(fields));
    }

    private static final JsonMasker MASKER = JsonMasker.getMasker(
            JsonMaskingConfig.builder()
                    // 手机号: 13800138000 -> 138****8000
                    .maskKeys(
                            Set.of("phone", "mobile"),
                            KeyMaskingConfig.builder().maskStringsWith(
                                    ValueMaskers.withRawValueFunction(JsonMaskerUtils::maskPhone)
                            ).build()
                    )
                    // 邮箱: zhangsan@example.com -> z***@example.com
                    .maskKeys(
                            Set.of("email"),
                            KeyMaskingConfig.builder().maskStringsWith(
                                    ValueMaskers.withRawValueFunction(JsonMaskerUtils::maskEmail)
                            ).build()
                    )
                    // 密码: 任意值 -> ******
                    .maskKeys(
                            Set.of("password", "pwd"),
                            KeyMaskingConfig.builder().maskStringsWith(
                                    ValueMaskers.with("******")
                            ).build()
                    )
                    // 身份证: 110101199001011234 -> 110************234
                    .maskKeys(
                            Set.of("idCard", "idCardNumber"),
                            KeyMaskingConfig.builder().maskStringsWith(
                                    ValueMaskers.withRawValueFunction(JsonMaskerUtils::maskIdCard)
                            ).build()
                    )
                    .build()
    );


    /**
     * @param jsonString JSON 字符串
     * @return 脱敏后的 JSON
     */
    public static String simpleMask(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            return jsonString;
        }
        return MASKER.mask(jsonString);
    }

    /**
     * 手机号脱敏
     * <p>
     * 13800138000 -> 138****8000
     */
    private static String maskPhone(String value) {
        if (value == null || value.length() < 7) {
            return "******";
        }

        return value.substring(0, 3)
                + "****"
                + value.substring(value.length() - 4);
    }

    /**
     * 邮箱脱敏
     * <p>
     * zhangsan@example.com -> z***@example.com
     */
    private static String maskEmail(String value) {
        if (value == null || value.isBlank()) {
            return "******";
        }

        int index = value.indexOf('@');

        if (index <= 0) {
            return "******";
        }

        String prefix = value.substring(0, index);
        String domain = value.substring(index);

        if (prefix.length() == 1) {
            return prefix + "***" + domain;
        }

        return prefix.substring(0, 1)
                + "***"
                + domain;
    }

    /**
     * 身份证脱敏
     * <p>
     * 110101199001011234 -> 110************234
     */
    private static String maskIdCard(String value) {
        if (value == null || value.length() <= 6) {
            return "******";
        }

        int prefixLength = 3;
        int suffixLength = 3;

        int maskLength = value.length() - prefixLength - suffixLength;

        if (maskLength <= 0) {
            return "******";
        }

        return value.substring(0, prefixLength)
                + "*".repeat(maskLength)
                + value.substring(value.length() - suffixLength);
    }
}
