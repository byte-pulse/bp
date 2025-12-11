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
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import static cloud.bytepulse.bp.common.utils.AuthUtils.getUserId;
import static cloud.bytepulse.bp.common.utils.ReqUtils.isPathMatching;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private final LoggingService loggingService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return !isPathMatching(LoggingConstant.NEED_LOGGING, requestURI);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    // 判断是否为 multipart 请求
    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().toLowerCase().startsWith("multipart/");
    }

    // 解析 multipart 请求内容, 文件字段记录为元信息 (不读文件内容)
    private String buildMultipartJson(HttpServletRequest request)
            throws IOException, ServletException {

        ObjectNode root = JsonUtils.OBJECT_MAPPER.createObjectNode();

        // 普通表单字段
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length == 1) {
                root.put(k, v[0]);
            } else {
                root.putPOJO(k, v);
            }
        });

        // 文件字段
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            if (part.getContentType() != null) {
                ObjectNode fileNode = JsonUtils.OBJECT_MAPPER.createObjectNode();
                fileNode.put("fileName", part.getSubmittedFileName());
                fileNode.put("size", part.getSize());
                fileNode.put("contentType", part.getContentType());
                root.set(part.getName(), fileNode);
            }
        }

        return JsonUtils.OBJECT_MAPPER.writeValueAsString(root);
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

        String requestBodyJson;

        // 判断请求是否包含文件
        if (isMultipart(request)) {
            requestBodyJson = buildMultipartJson(request);
        } else {
            requestBodyJson = requestWrapper.getBodyString();
        }

        String resolvedException = null;

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception e) {
            resolvedException = e.getMessage();
            throw e;
        } finally {

            long cost = System.currentTimeMillis() - startTime;

            // 判断响应是否为文件
            boolean isFileResponse =
                    response.getContentType() != null &&
                            (
                                    response.getContentType().contains("application/octet-stream")
                                            || response.getHeader("Content-Disposition") != null
                            );

            String rawResponseBody;
            String finalJson;

            if (isFileResponse) {
                rawResponseBody = "文件";
                finalJson = "文件";
            } else {
                rawResponseBody = new String(responseWrapper.getBody());

                try {
                    JsonNode original = JsonUtils.OBJECT_MAPPER.readTree(rawResponseBody);
                    ObjectNode obj = (ObjectNode) original;
                    obj.put("timestamp", System.currentTimeMillis());
                    obj.put("traceId", traceId);

                    JsonNode eNode = obj.get("e");
                    resolvedException = eNode != null ? eNode.asText() : null;

                    obj.remove("e");
                    finalJson = JsonUtils.OBJECT_MAPPER.writeValueAsString(obj);

                } catch (Exception ex) {
                    finalJson = rawResponseBody;
                }
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
                    requestBodyJson,
                    finalJson,
                    resolvedException != null ? resolvedException : "无异常"
            );

            SysLog sysLog = new SysLog();
            sysLog.setTraceId(traceId);
            sysLog.setUri(request.getRequestURI());
            sysLog.setHttpMethod(request.getMethod());
            sysLog.setQueryParams(request.getQueryString());
            sysLog.setBodyParams(requestBodyJson);
            sysLog.setResponseResult(rawResponseBody);
            sysLog.setRequestTime(new Date(startTime));
            sysLog.setRequestIp(ReqUtils.getIP());
            sysLog.setUserId(getUserId());
            sysLog.setCost(cost);
            sysLog.setException(resolvedException);

            loggingService.save(sysLog);

            responseWrapper.setBody(finalJson.getBytes());
            responseWrapper.copyToResponse();

            TraceIdUtil.clear();
        }
    }
}
