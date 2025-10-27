package cloud.bytepulse.cache.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * spring redis 工具类
 *
 * @author jiejiebiezheyang
 * @since 2024-03-02 18:00
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCache {

    public final RedisTemplate<String, Object> redisTemplate;

    public final StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存基本的对象, Integer | 实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象, Integer | 实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, Expiration.from(timeout, timeUnit));
    }

    /**
     * 缓存基本的 String
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public void setCacheString(final String key, final String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的 String
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public void setCacheString(final String key, final String value, final Long timeout, final TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, Expiration.from(timeout, timeUnit));
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis 键
     * @param timeout 超时时间
     * @return true = 设置成功; false = 设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis 键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true = 设置成功; false = 设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, Expiration.from(timeout, unit));
    }

    /**
     * 获取有效时间
     *
     * @param key Redis 键
     * @return 有效时间
     */
    public long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 获取有效时间
     *
     * @param key  Redis 键
     * @param unit 时间单位
     * @return 有效时间
     */
    public long getExpire(final String key, final TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在 false 不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
