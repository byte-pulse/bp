package cloud.bytepulse.bp.common.util.json;

import cloud.bytepulse.bp.framework.exception.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

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
        if (o == null) {
            super.add(NullNode.instance);
        } else {
            super.add(JsonUtils.OBJECT_MAPPER.valueToTree(o));
        }
        return this;
    }

    /**
     * 获取 JsonObj 节点
     */
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

    /**
     * 获取 JsonArr 节点
     */
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

    /**
     * 获取 int 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Integer getInt(int index, Integer... defaultValue) {
        JsonNode jsonNode = super.get(index);
        return JsonUtils.getInt(jsonNode, defaultValue);
    }

    /**
     * 获取 long 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Long getLong(int index, Long... defaultValue) {
        JsonNode jsonNode = super.get(index);
        return JsonUtils.getLong(jsonNode, defaultValue);
    }

    /**
     * 获取 double 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Double getDouble(int index, Double... defaultValue) {
        JsonNode jsonNode = super.get(index);
        return JsonUtils.getDouble(jsonNode, defaultValue);
    }

    /**
     * 获取 decimal 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public BigDecimal getBigDecimal(int index, BigDecimal... defaultValue) {
        JsonNode jsonNode = super.get(index);
        return JsonUtils.getBigDecimal(jsonNode, defaultValue);
    }

    /**
     * 获取 boolean 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Boolean getBoolean(int index, Boolean... defaultValue) {
        JsonNode jsonNode = super.get(index);
        return JsonUtils.getBoolean(jsonNode, defaultValue);
    }

    /**
     * 获取 boolean 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public String getString(int index, String... defaultValue) {
        JsonNode jsonNode = super.get(index);
        return JsonUtils.getString(jsonNode, defaultValue);
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
