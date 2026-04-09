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

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        return clazz.cast(value);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public String getCacheString(final String key) {
        ValueOperations<String, String> operation = stringRedisTemplate.opsForValue();
        return operation.get(key);
    }


    /**
     * 删除单个对象
     *
     * @param key 缓存键值
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return true 成功 false 失败
     */
    public boolean deleteObject(final Collection<String> collection) {
        return redisTemplate.delete(collection) > 0;
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的 List 数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key, Class<T> clazz) {
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        if (range == null || range.isEmpty()) {
            return Collections.emptyList();
        }
        return range.stream()
                .map(clazz::cast)
                .toList();
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, Object> setCacheSet(final String key, final Set<T> dataSet, Class<T> clazz) {
        BoundSetOperations<String, Object> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存键值
     * @return 缓存数据的对象
     */
    public <T> Set<T> getCacheSet(final String key, Class<T> clazz) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }
}
