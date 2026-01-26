package cloud.bytepulse.bp.framework.filtter;

import cloud.bytepulse.bp.app.logging.service.LoggingService;
import cloud.bytepulse.bp.common.util.ReqUtils;
import cloud.bytepulse.bp.common.util.TraceIdUtils;
import cloud.bytepulse.bp.common.util.json.JsonUtils;
import cloud.bytepulse.bp.domain.entity.SysLog;
import cloud.bytepulse.bp.framework.constant.LoggingConstant;
import cloud.bytepulse.bp.framework.http.wrapper.LoggingCachedBodyRequestWrapper;
import cloud.bytepulse.bp.framework.http.wrapper.LoggingCachedBodyResponseWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static cloud.bytepulse.bp.common.util.AuthUtils.getUserId;
import static cloud.bytepulse.bp.common.util.ReqUtils.isPathMatching;

/**
 * 请求日志过滤器
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
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

    // 判断是否为 multipart 请求
//    private boolean isMultipart(HttpServletRequest request) {
//        return request.getContentType() != null
//                && request.getContentType().toLowerCase().startsWith("multipart/");
//    }

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

        // 用 Map 收集同名文件
        Map<String, ArrayNode> fileMap = new HashMap<>();

        for (Part part : parts) {
            if (part.getContentType() != null) {
                ObjectNode fileNode = JsonUtils.OBJECT_MAPPER.createObjectNode();
                fileNode.put("fileName", part.getSubmittedFileName());
                fileNode.put("size", part.getSize());
                fileNode.put("contentType", part.getContentType());

                // 如果是同名文件，就放到数组里
                fileMap.computeIfAbsent(part.getName(), k -> JsonUtils.OBJECT_MAPPER.createArrayNode())
                        .add(fileNode);
            }
        }

        // 放回 root
        fileMap.forEach(root::set);

        return JsonUtils.OBJECT_MAPPER.writeValueAsString(root);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("LoggingFilter");

        response.setCharacterEncoding("utf-8");


        long startTime = System.currentTimeMillis();
        String traceId = TraceIdUtils.init();

        LoggingCachedBodyRequestWrapper requestWrapper = new LoggingCachedBodyRequestWrapper(request);
        LoggingCachedBodyResponseWrapper responseWrapper = new LoggingCachedBodyResponseWrapper(response);


        String resolvedException = null;

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } catch (Exception e) {
            resolvedException = e.getMessage();
            throw e;
        } finally {
            // 用来保存日志
            String requestBodyJson;

            // 判断请求是否包含文件
            if (requestWrapper.isMultipartRequest()) {
                requestBodyJson = buildMultipartJson(request);
            } else {
                requestBodyJson = requestWrapper.getContentAsString();
            }

            long cost = System.currentTimeMillis() - startTime;

            // 判断响应是否为文件
            boolean isFileResponse = responseWrapper.isFileResponse();
            // 返回的
            String rawResponseBody = new String(responseWrapper.getContentAsByteArray());
            // 用来记录的
            String finalJson;

            if (isFileResponse) {
                ObjectNode fileNode = JsonUtils.OBJECT_MAPPER.createObjectNode();
                fileNode.put("size", responseWrapper.getContentSize());
                fileNode.put("contentType", responseWrapper.getContentType());
                fileNode.put("fileName", responseWrapper.getFileNameFromResponse());
                finalJson = JsonUtils.OBJECT_MAPPER.writeValueAsString(fileNode);
            } else {
                try {
                    if (responseWrapper.getStatus() > 300 && responseWrapper.getStatus() < 400) {
                        finalJson = responseWrapper.getStatus() + " " + responseWrapper.getHeader("Location");
                    } else {
                        JsonNode original = JsonUtils.OBJECT_MAPPER.readTree(rawResponseBody);
                        ObjectNode obj = (ObjectNode) original;
                        obj.put("timestamp", System.currentTimeMillis());
                        obj.put("traceId", traceId);

                        JsonNode eNode = obj.get("e");
                        resolvedException = eNode != null ? eNode.asText() : null;
                        obj.remove("e");

                        finalJson = rawResponseBody = JsonUtils.OBJECT_MAPPER.writeValueAsString(obj);
                    }


                } catch (Exception ex) {
                    isFileResponse = true;
                    ObjectNode fileNode = JsonUtils.OBJECT_MAPPER.createObjectNode();
                    fileNode.put("size", responseWrapper.getContentSize());
                    fileNode.put("contentType", responseWrapper.getContentType());
                    fileNode.put("fileName", responseWrapper.getFileNameFromResponse());
                    finalJson = JsonUtils.OBJECT_MAPPER.writeValueAsString(fileNode);
                }
            }

            requestBodyJson = requestBodyJson.equals("null") ? null : requestBodyJson;

            // 控制台打印
            log.info("接口调用 [TraceId={}] {} {}ms req = {} resp = {} ex = {}",
                    traceId,
                    request.getRequestURI(),
                    cost,
                    requestBodyJson,
                    finalJson,
                    resolvedException != null ? resolvedException : "无异常"
            );


            // 存入数据库
            SysLog sysLog = new SysLog();
            sysLog.setTraceId(traceId);
            sysLog.setUri(request.getRequestURI());
            sysLog.setHttpMethod(request.getMethod());
            sysLog.setQueryParams(request.getQueryString());
            sysLog.setBodyParams(requestBodyJson);
            sysLog.setResponseResult(finalJson);
            sysLog.setRequestTime(new Date(startTime));
            sysLog.setRequestIp(ReqUtils.getIP());
            sysLog.setUserId(getUserId());
            sysLog.setCost(cost);
            sysLog.setException(resolvedException);

            loggingService.save(sysLog);

            // 修改返回前端
            if (!isFileResponse) {
                responseWrapper.resetBuffer();
                responseWrapper.getOutputStream().write(rawResponseBody.getBytes());
            }
            responseWrapper.copyBodyToResponse();
            TraceIdUtils.clear();
        }
    }
}