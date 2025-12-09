package cloud.bytepulse.bp.framwork.aspect;

import cloud.bytepulse.bp.common.utils.AuthUtils;
import cloud.bytepulse.bp.common.utils.ReqUtils;
import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.domain.mapper.SysLogMapper;
import cloud.bytepulse.bp.domain.models.entity.SysLog;
import cloud.bytepulse.bp.framwork.annotation.Log;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.Optional;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-11 14:00
 */
@Aspect
@Component
@Order(2)
@RequiredArgsConstructor
public class LogAspect {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LogAspect.class);

    private final SysLogMapper sysLogMapper;

    @Pointcut("@annotation(cloud.bytepulse.bp.framwork.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    private Object testAop(ProceedingJoinPoint point) throws Throwable {
        // 获取当前请求
        HttpServletRequest request = ReqUtils.getRequest();
        // 获取参数
        Object[] args = point.getArgs();
        // 获取目标方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Log log = method.getAnnotation(Log.class);

        long startTime = System.currentTimeMillis();
        ApiResponse result = null;
        try {
            Object proceed = point.proceed(); // 执行目标方法 获取返回值
            if (proceed instanceof ApiResponse) {
                result = (ApiResponse) proceed;
            }
            long cost = System.currentTimeMillis() - startTime;
            // 保存
            log(log, request, result, signature, method, args, cost);
            return proceed;
        } catch (Exception e) {
            // 保存
            log(log, request, ApiResponse.error(e.getMessage()), signature, method, args, 0);
            throw e;
        }
    }

    @Async
    public void log(Log log, HttpServletRequest request, ApiResponse result, MethodSignature signature, Method method, Object[] args, long cost) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 保存日志
        SysLog sysLog = new SysLog();
        sysLog.setApiName(log.value()); // 接口名称
        sysLog.setUri(request.getRequestURI()); // uri
        sysLog.setHttpMethod(request.getMethod()); // 请求方式
        sysLog.setQueryParams(request.getQueryString()); // 查询参数
        // 获取方法的参数列表
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            // 判断是否为请求体
            if (parameter.isAnnotationPresent(RequestBody.class)) {
                String bodyJson = mapper.writeValueAsString(args[i]);
                sysLog.setBodyParams("null".equals(bodyJson) ? "" : bodyJson);
                break;
            } else if (MultipartFile.class.isAssignableFrom(parameter.getType())
                    || (parameter.getType().isArray() && MultipartFile.class.isAssignableFrom(parameter.getType().getComponentType()))) {
                sysLog.setBodyParams("文件");
            }
        }
        sysLog.setResponseResult(mapper.writeValueAsString(result)); // 结果
        sysLog.setMethod(signature.getDeclaringTypeName() + "." + method.getName()); // 执行的方法
        sysLog.setRequestTime(new Date()); // 执行时间戳
        sysLog.setRequestIp(ReqUtils.getIP(request)); // 请求者ip
        sysLog.setUserId(AuthUtils.getUserId()); // 调用者id
        sysLog.setCost(cost); // 耗时
        boolean persist = log.persist();
        // 持久化存入数据库
        if (persist) {
            sysLogMapper.insert(sysLog);
        }
        logger.info("接口调用\nURL:\t{}\n请求方式:\t{}\n查询参数:\t{}\n请求参数:\t{}\n请求时间:\t{}\n请求IP:\t{}\n耗时:\t{}",
                sysLog.getUri(),
                sysLog.getHttpMethod(),
                Optional.ofNullable(sysLog.getQueryParams()).orElse(""),
                Optional.ofNullable(sysLog.getBodyParams()).orElse(""),
                sysLog.getRequestTime(),
                sysLog.getRequestIp(),
                sysLog.getCost()
        );
    }
}
