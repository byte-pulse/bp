package cloud.bytepulse.bp.common.util.json;

import cloud.bytepulse.bp.framework.exception.BytePulseException;
import cloud.bytepulse.bp.framework.exception.JsonParseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * json 工具类
 *
 * @author jiejiebiezheyang
 * @since 2024-03-11 20:00
 */
public class JsonUtils {

    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);// null 值也序列化

        OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);// 空对象不会报错

        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);// JSON 有额外字段不会报错
        OBJECT_MAPPER.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);// 单值不会自动转数组
        OBJECT_MAPPER.disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);// 空字符串不当作 null
    }


    /**
     * 实体转 json 字符串
     */
    public static String toJsonStr(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BytePulseException(e.getMessage());
        }
    }

    /**
     * 实体转 json 字符串
     */
    public static String toPrettyJsonStr(Object object) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BytePulseException(e.getMessage());
        }
    }

    /**
     * 解析为 JsonObj
     *
     */
    public static JsonObj parseJsonObj(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, JsonObj.class);
        } catch (JsonProcessingException e) {
            throw new JsonParseException("解析为 JsonObj 失败");
        }
    }

    /**
     * 解析为 JsonObj
     *
     */
    public static JsonObj parseJsonObj(JsonNode jsonNode) {
        if (!jsonNode.isObject()) {
            throw new JsonParseException("非 object 类型");
        }
        try {
            return OBJECT_MAPPER.convertValue(jsonNode, JsonObj.class);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }


    /**
     * 解析为 JsonArr
     */
    public static JsonArr parseJsonArr(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, JsonArr.class);
        } catch (JsonProcessingException e) {
            throw new JsonParseException("解析为 JsonArr 失败");
        }
    }


    /**
     * 解析为 JsonArr
     */
    public static JsonArr parseJsonArr(JsonNode jsonNode) {
        if (!jsonNode.isArray()) {
            throw new JsonParseException("非 JsonArr 类型");
        }
        try {
            return OBJECT_MAPPER.convertValue(jsonNode, JsonArr.class);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    /**
     * 直接解析基础类型 JsonNode
     * - 数字 → Integer / Long / Double / BigDecimal
     * - 布尔 → Boolean
     * - 字符串 → String
     * - null → null
     */
    public static Object parseBasicValue(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return null;
        }

        if (jsonNode.isInt()) return jsonNode.asInt();
        if (jsonNode.isLong()) return jsonNode.asLong();
        if (jsonNode.isDouble()) return jsonNode.asDouble();
        if (jsonNode.isBigDecimal()) return jsonNode.decimalValue();
        if (jsonNode.isBoolean()) return jsonNode.asBoolean();
        if (jsonNode.isTextual()) return jsonNode.asText();

        return null;
    }


    /**
     * 自动解析
     */
    public static Object parseValue(JsonNode node) {
        Object o = parseBasicValue(node);
        if (o != null) {
            return o;
        } else {
            if (node.isObject()) {
                return parseJsonObj(node);
            } else if (node.isArray()) {
                return parseJsonArr(node);
            }
        }
        return null;
    }
}
