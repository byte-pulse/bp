package cloud.bytepulse.cache.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.introspect.VisibilityChecker;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.Objects;

/**
 * @author jiejiebiezheyang
 * @since 2024-06-26 09:00
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisPrefixSerializer redisPrefixSerializer;

    /**
     * 序列化方式
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("cloud.bytepulse.")
                .allowIfSubType("java.util.")
                .build();

        JsonMapper mapper = JsonMapper.builder().changeDefaultVisibility(v -> new VisibilityChecker(
                        JsonAutoDetect.Visibility.ANY, // field
                        JsonAutoDetect.Visibility.ANY, // getter
                        JsonAutoDetect.Visibility.ANY, // is-getter
                        JsonAutoDetect.Visibility.ANY, // setter
                        JsonAutoDetect.Visibility.ANY, // creator -- NOTE: was `ANY` for 2.x
                        JsonAutoDetect.Visibility.ANY // scalar-constructor (new in 3.x)
                ))
                .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        return getStringObjectRedisTemplate(redisConnectionFactory, mapper);
    }

    /**
     * 设置序列化
     */
    private RedisTemplate<String, Object> getStringObjectRedisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        JacksonJsonRedisSerializer<Object> serializer = new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(redisPrefixSerializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(redisPrefixSerializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    /**
     * 字符串序列化方式
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(redisPrefixSerializer);
        return template;
    }

    /**
     * 缓存管理器
     *
     * @param redisTemplate redisTemplate
     * @return Redis缓存管理器
     */
    @Bean
    public RedisCacheManager redisCacheManager(@Qualifier("redisTemplate") RedisTemplate<?, ?> redisTemplate) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }
}
