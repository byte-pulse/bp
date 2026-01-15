package cloud.bytepulse.bp.common.utils.json;

import cloud.bytepulse.bp.framework.exception.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-15 19:56
 */
public class JsonObj extends HashMap<String, JsonNode> {

    /**
     * 添加元素, 允许链式调用
     */
    public JsonObj set(String key, Object value) {
        super.put(key, JsonUtils.OBJECT_MAPPER.valueToTree(value));
        return this;
    }

    public JsonObj getJsonObj(String key) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isObject()) {
            throw new JsonParseException("非 object 类型");
        }
        try {
            return JsonUtils.OBJECT_MAPPER.convertValue(jsonNode, JsonObj.class);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public JsonArr getJsonArr(String key) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isArray()) {
            throw new JsonParseException("非 array 类型");
        }
        try {
            return JsonUtils.OBJECT_MAPPER.convertValue(jsonNode, JsonArr.class);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public int getInt(String key, int... defaultValue) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isInt()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 int 类型");
            }
        }
        return jsonNode.asInt();
    }

    public long getLong(String key, long... defaultValue) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isNumber()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 long 类型");
            }
        }
        return jsonNode.asLong();
    }

    public double getDouble(String key, double... defaultValue) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isNumber()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 double 类型");
            }
        }
        return jsonNode.asDouble();
    }

    public BigDecimal getBigDecimal(String key, BigDecimal... defaultValue) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isNumber()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 BigDecimal 类型");
            }
        }
        return jsonNode.decimalValue();
    }

    public boolean getBoolean(String key, boolean... defaultValue) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isBoolean()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 boolean 类型");
            }
        }
        return jsonNode.asBoolean();
    }

    public String getString(String key, String... defaultValue) {
        JsonNode jsonNode = super.get(key);
        if (jsonNode == null) {
            if (defaultValue.length > 0) return defaultValue[0];
            throw new JsonParseException("Key '" + key + "' 不存在");
        }
        if (!jsonNode.isTextual()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 string 类型");
            }
        }
        return jsonNode.asText();
    }

    /**
     * 转为 json 字符串
     */
    @Override
    public String toString() {
        return JsonUtils.toJsonStr(this);
    }

    public String toPrettyString() {
        return JsonUtils.toPrettyJsonStr(this);
    }
}
