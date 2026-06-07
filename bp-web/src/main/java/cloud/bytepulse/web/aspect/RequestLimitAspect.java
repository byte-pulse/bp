package cloud.bytepulse.web.aspect;


import cloud.bytepulse.common.core.annotation.RequestLimit;
import cloud.bytepulse.common.core.model.ApiResponse;
import cloud.bytepulse.common.core.util.ReqUtils;
import cloud.bytepulse.security.Auths;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 接口请求限制
 *
 * @author jiejiebiezheyang
 * @since 2024-05-07 14:00
 */
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class RequestLimitAspect {

    public final RedisTemplate<String, Object> redisTemplate;

    private static @NonNull DefaultRedisScript<Long> getLongDefaultRedisScript() {
        String lua = """
                local key = KEYS[1]
                local limit = tonumber(ARGV[1])
                local expire = tonumber(ARGV[2])
                local count = redis.call('INCR', key)
                if count == 1 then
                  redis.call('PEXPIRE', key, expire)
                end
                if count > limit then
                  return 0
                end
                return 1
                """;

        // 4. 执行 Lua
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(lua);
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
