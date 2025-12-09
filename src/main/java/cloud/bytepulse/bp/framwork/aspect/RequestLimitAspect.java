package cloud.bytepulse.bp.framwork.aspect;

import cloud.bytepulse.bp.common.utils.RedisUtils;
import cloud.bytepulse.bp.common.utils.ReqUtils;
import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.framwork.annotation.RequestLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static cloud.bytepulse.bp.common.utils.ReqUtils.getIP;

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

    private final RedisUtils redisUtils;


    @Pointcut("@annotation(cloud.bytepulse.bp.framwork.annotation.RequestLimit)")
    public void requestLimitPointCut() {
    }

    @Around("requestLimitPointCut()")
    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        // 获得request对象
        HttpServletRequest request = ReqUtils.getRequest();
        // 获取目标方法 和 注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequestLimit requestLimit = method.getAnnotation(RequestLimit.class);

        String redisKey = "wx:requestLimit:" + request.getRequestURI() + ":" + getIP();
        Integer ipCnt = redisUtils.getCacheObject(redisKey);
        int uCount = ipCnt == null ? 0 : ipCnt;
        if (uCount >= requestLimit.count()) { // 超过次数，不执行目标方法
            return ApiResponse.error("请求过于频繁，请稍后再试");
        } else {
            //请求时，设置有效时间, 记录加一
            redisUtils.setCacheObject(redisKey, uCount + 1, requestLimit.time(), TimeUnit.MILLISECONDS);
        }
        // result的值就是被拦截方法的返回值
        return point.proceed();
    }

}
