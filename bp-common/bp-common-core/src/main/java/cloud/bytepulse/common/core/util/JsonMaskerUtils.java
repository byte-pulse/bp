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
}
