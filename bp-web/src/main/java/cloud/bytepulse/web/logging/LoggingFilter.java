package cloud.bytepulse.web.logging;

import cloud.bytepulse.common.core.component.LoggingFilterInterface;
import cloud.bytepulse.common.core.constant.ApiPathRegistry;
import cloud.bytepulse.common.core.model.ApiLoggingInfo;
import cloud.bytepulse.common.core.util.JsonMaskerUtils;
import cloud.bytepulse.common.core.util.ReqUtils;
import cloud.bytepulse.common.core.util.TraceIdUtils;
import cloud.bytepulse.data.entity.SysLog;
import cloud.bytepulse.security.Auths;
import cloud.bytepulse.service.logging.LoggingService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.util.mime.MimeUtility;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 16:37
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter extends LoggingFilterInterface {

    private final LoggingService LoggingService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String traceId = TraceIdUtils.init();
        String requestURI = request.getRequestURI();

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
            if (isMultipart(requestWrapper)) {
                requestBodyJson = buildMultipartJson(request);
            } else {
                requestBodyJson = requestWrapper.getContentAsString();
            }

            long cost = System.currentTimeMillis() - startTime;

            // 判断响应是否为文件
            boolean isFileResponse = isFileResponse(responseWrapper);
            // 返回的
            String rawResponseBody = new String(responseWrapper.getContentAsByteArray());
            // 用来记录的
            String finalJson;

            if (isFileResponse) {
                // 如果是文件
                JSONObject fileNode = new JSONObject();
                fileNode.put("size", responseWrapper.getContentSize());
                fileNode.put("contentType", responseWrapper.getContentType());
                fileNode.put("fileName", getFileNameFromResponse(responseWrapper));
                // 最终日志保存的
                finalJson = fileNode.toJSONString();
            } else {
                try {
                    if (responseWrapper.getStatus() > 300 && responseWrapper.getStatus() < 400) {
                        // 记录重定向
                        finalJson = responseWrapper.getStatus() + " " + responseWrapper.getHeader("Location");
                    } else {
                        JSONObject obj = JSON.parseObject(rawResponseBody);
                        obj.put("traceId", traceId); // 追踪 id
                        obj.put("timestamp", System.currentTimeMillis()); // 时间戳

                        resolvedException = obj.getString("e");
                        obj.remove("e");

                        finalJson = rawResponseBody = obj.toJSONString();
                    }
                } catch (Exception ex) {
                    isFileResponse = true;
                    JSONObject fileNode = new JSONObject();
                    fileNode.put("size", responseWrapper.getContentSize());
                    fileNode.put("contentType", responseWrapper.getContentType());
                    fileNode.put("fileName", getFileNameFromResponse(responseWrapper));
                    finalJson = fileNode.toJSONString();
                }
            }

            requestBodyJson = requestBodyJson.equals("null") ? null : requestBodyJson;
            // 数据脱敏
            requestBodyJson = JsonMaskerUtils.simpleMask(requestBodyJson);
            finalJson = JsonMaskerUtils.simpleMask(finalJson);

}
