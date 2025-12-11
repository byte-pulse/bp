package cloud.bytepulse.bp.framwork.filtter;

import cloud.bytepulse.bp.app.logging.service.LoggingService;
import cloud.bytepulse.bp.common.utils.JsonUtils;
import cloud.bytepulse.bp.common.utils.ReqUtils;
import cloud.bytepulse.bp.common.utils.TraceIdUtil;
import cloud.bytepulse.bp.domain.models.entity.SysLog;
import cloud.bytepulse.bp.framwork.constant.LoggingConstant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

import static cloud.bytepulse.bp.common.utils.AuthUtils.getUserId;
import static cloud.bytepulse.bp.common.utils.ReqUtils.isPathMatching;

/**
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private final LoggingService loggingService;

    /**
     * 需要记录的不放心
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // 需要放行的端口直接放行
        return !isPathMatching(LoggingConstant.NEED_LOGGING, requestURI);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String traceId = TraceIdUtil.init();

        CachedBodyRequestWrapper requestWrapper = new CachedBodyRequestWrapper(request);
        CachedBodyResponseWrapper responseWrapper = new CachedBodyResponseWrapper(response);

        String requestBody = requestWrapper.getBodyString();

        String resolvedException = null;

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception e) {
            resolvedException = e.getMessage();
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - startTime;
            String responseBody = new String(responseWrapper.getBody());

            String finalJson;
            try {
                // 尝试把原响应解析成 JSON 对象
                JsonNode original = JsonUtils.OBJECT_MAPPER.readTree(responseBody);
                ObjectNode objectNode = (ObjectNode) original;
                objectNode.put("timestamp", System.currentTimeMillis());
                objectNode.put("traceId", traceId);
                JsonNode eNode = objectNode.get("e");
                resolvedException = eNode != null ? eNode.asText() : null;
                objectNode.remove("e");
                finalJson = JsonUtils.OBJECT_MAPPER.writeValueAsString(objectNode);

            } catch (Exception ex) {
                // 如果不是 JSON (例如文件下载),那就原样返回
                finalJson = responseBody;
            }

            log.info(
                    """     
                            \u001B[35m接口调用
                            \u001B[35m[TraceId= \u001B[0m{}\
                            \u001B[35m] \u001B[0m{} {}ms
                            \u001B[35mreq= \u001B[0m{}
                            \u001B[35mresp= \u001B[0m{}
                            \u001B[35mex= \u001B[0m{}""",
                    traceId,
                    request.getRequestURI(),
                    cost,
                    requestBody,
                    finalJson,
                    resolvedException != null ? resolvedException : "无异常"
            );

            String finalResolvedException = resolvedException;

            SysLog sysLog = new SysLog();
            sysLog.setTraceId(traceId);
            sysLog.setUri(request.getRequestURI());
            sysLog.setHttpMethod(request.getMethod());
            sysLog.setQueryParams(request.getQueryString());
            sysLog.setBodyParams(requestBody);
            sysLog.setResponseResult(responseBody);
            sysLog.setRequestTime(new Date(startTime));
            sysLog.setRequestIp(ReqUtils.getIP());
            sysLog.setUserId(getUserId());
            sysLog.setCost(cost);
            sysLog.setException(finalResolvedException);
            loggingService.save(sysLog);

            // 写回响应给客户端
            responseWrapper.setBody(finalJson.getBytes());
            responseWrapper.copyToResponse();

            TraceIdUtil.clear();
        }
    }
}
