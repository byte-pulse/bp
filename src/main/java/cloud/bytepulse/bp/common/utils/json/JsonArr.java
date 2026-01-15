package cloud.bytepulse.bp.common.utils.json;

import cloud.bytepulse.bp.framework.exception.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-15 19:55
 */
public class JsonArr extends ArrayList<JsonNode> {

    /**
     * 添加元素
     * 允许链式调用
     */
    public JsonArr append(Object o) {
        super.add(JsonUtils.OBJECT_MAPPER.valueToTree(o));
        return this;
    }

    public JsonObj getJsonObj(int index) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isObject()) {
            throw new JsonParseException("非 object 类型");
        }
        try {
            return JsonUtils.OBJECT_MAPPER.convertValue(jsonNode, JsonObj.class);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public JsonArr getJsonArr(int index) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isArray()) {
            throw new JsonParseException("非 array 类型");
        }
        try {
            return JsonUtils.OBJECT_MAPPER.convertValue(jsonNode, JsonArr.class);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }

    public int getInt(int index, int... defaultValue) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isInt()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 int 类型");
            }
        }
        return jsonNode.asInt();
    }

    public long getLong(int index, long... defaultValue) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isNumber()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 long 类型");
            }
        }
        return jsonNode.asLong();
    }

    public double getDouble(int index, double... defaultValue) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isNumber()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 double 类型");
            }
        }
        return jsonNode.asDouble();
    }

    public BigDecimal getBigDecimal(int index, BigDecimal... defaultValue) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isNumber()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 BigDecimal 类型");
            }
        }
        return jsonNode.decimalValue();
    }

    public boolean getBoolean(int index, boolean... defaultValue) {
        JsonNode jsonNode = super.get(index);
        if (!jsonNode.isBoolean()) {
            if (defaultValue.length > 0) {
                return defaultValue[0];
            } else {
                throw new JsonParseException("非 boolean 类型");
            }
        }
        return jsonNode.asBoolean();
    }

    public String getString(int index, String... defaultValue) {
        JsonNode jsonNode = super.get(index);
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
