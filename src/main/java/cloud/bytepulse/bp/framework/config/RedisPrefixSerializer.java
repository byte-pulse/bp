package cloud.bytepulse.bp.framework.config;

import cloud.bytepulse.bp.core.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * @author jiejiebiezheyang
 * @since 2024-06-26 09:00
 */
@Configuration
@RequiredArgsConstructor
public class RedisPrefixSerializer extends StringRedisSerializer {

    private final AppProperties appProperties;

    /**
     * 序列化
     *
     * @param s key
     * @return 结果
     */
    @Override
    public byte[] serialize(String s) {
        if (s == null) {
            return new byte[0];
        }
        String realKey = appProperties.getRedis().getPrefix() + s;
        return super.serialize(realKey);
    }

    /**
     * 反序列化
     *
     * @param bytes 数据
     * @return 结果
     */
    @Override
    public String deserialize(byte[] bytes) {
        String s = bytes == null ? null : new String(bytes);
        if (!StringUtils.hasText(s)) {
            return s;
        }
        int index = s.indexOf(appProperties.getRedis().getPrefix());
        if (index != -1) {
            return s.substring(appProperties.getRedis().getPrefix().length());
        }
        return s;
    }
}
