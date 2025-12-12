package cloud.bytepulse.bp.common.utils;

import cloud.bytepulse.bp.framework.exception.BytePulseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json 工具类
 *
 * @author jiejiebiezheyang
 * @since 2024-03-11 20:00
 */
public class JsonUtils {

    public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String toJsonStr(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BytePulseException(e.getMessage());
        }
    }

}
