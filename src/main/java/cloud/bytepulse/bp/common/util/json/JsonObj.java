package cloud.bytepulse.bp.common.util.json;

import cloud.bytepulse.bp.framework.exception.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

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
        if (value == null) {
            // 可以传 null
            super.put(key, NullNode.instance);
        } else {
            super.put(key, JsonUtils.OBJECT_MAPPER.valueToTree(value));
        }
        return this;
    }

    /**
     * 转为实体
     */
    public <T> T toBean(Class<T> clazz) {
        return JsonUtils.OBJECT_MAPPER.convertValue(this, clazz);
    }

    /**
     * 获取 JsonObj 节点
     */
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

    /**
     * 获取 JsonArr 节点
     */
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

    /**
     * 获取 int 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Integer getInt(String key, Integer... defaultValue) {
        JsonNode jsonNode = super.get(key);
        return JsonUtils.getInt(jsonNode, defaultValue);
    }

    /**
     * 获取 long 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Long getLong(String key, Long... defaultValue) {
        JsonNode jsonNode = super.get(key);
        return JsonUtils.getLong(jsonNode, defaultValue);
    }

    /**
     * 获取 double 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Double getDouble(String key, Double... defaultValue) {
        JsonNode jsonNode = super.get(key);
        return JsonUtils.getDouble(jsonNode, defaultValue);
    }

    /**
     * 获取 decimal 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public BigDecimal getBigDecimal(String key, BigDecimal... defaultValue) {
        JsonNode jsonNode = super.get(key);
        return JsonUtils.getBigDecimal(jsonNode, defaultValue);
    }

    /**
     * 获取 boolean 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public Boolean getBoolean(String key, Boolean... defaultValue) {
        JsonNode jsonNode = super.get(key);
        return JsonUtils.getBoolean(jsonNode, defaultValue);
    }

    /**
     * 获取 boolean 类型
     *
     * <p>key 不存在时, 返回 defaultValue, 没设置 defaultValue 时, 则返回 null</p>
     */
    public String getString(String key, String... defaultValue) {
        JsonNode jsonNode = super.get(key);
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
