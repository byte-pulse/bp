package cloud.bytepulse.web.logging;

import cloud.bytepulse.common.core.component.LoggingFilterInterface;
import cloud.bytepulse.common.core.constant.ApiPathRegistry;
import cloud.bytepulse.common.core.model.ApiLoggingInfo;
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

            String method = request.getMethod();
            ApiLoggingInfo apiLoggingInfo = ApiPathRegistry.LOGGING_API.get(method)
                    .stream()
                    .filter(api -> ReqUtils.isPathMatching(api.getPath(), requestURI))
                    .findFirst()
                    .orElseGet(ApiLoggingInfo::new);

            // 日志打印
            log.debug("接口调用 [TraceId={}] {} {} {} {}ms req = {} resp = {} ex = {}",
                    traceId,
                    apiLoggingInfo.getOperate().getDesc(),
                    apiLoggingInfo.getDesc(),
                    request.getRequestURI(),
                    cost,
                    requestBodyJson,
                    finalJson,
                    resolvedException != null ? resolvedException : "无异常"
            );
            Set<String> loggingApiCollect = ApiPathRegistry.LOGGING_API.get(method)
                    .stream().map(ApiLoggingInfo::getPath)
                    .collect(Collectors.toSet());
            // 需要日志, 存入数据库
            if (ReqUtils.isPathMatching(loggingApiCollect, requestURI)) {
                // 存入数据库
                SysLog sysLog = new SysLog();
                sysLog.setTraceId(traceId);
                sysLog.setOperate(apiLoggingInfo.getOperate().getDesc());
                sysLog.setDescription(apiLoggingInfo.getDesc());
                sysLog.setUri(request.getRequestURI());
                sysLog.setHttpMethod(request.getMethod());
                sysLog.setQueryParams(request.getQueryString());
                sysLog.setBodyParams(requestBodyJson);
                sysLog.setResponseResult(finalJson);
                sysLog.setRequestTime(new Date(startTime));
                sysLog.setRequestIp(ReqUtils.getIP());
                sysLog.setUserId(Auths.getUserId());
                sysLog.setCost(cost);
                sysLog.setException(resolvedException);
                try {
                    LoggingService.asyncSaveLog(sysLog);
                } catch (Exception e) {
                    log.error("日志保存失败", e);
                }
            }

            // 修改返回前端
            if (!isFileResponse) {
                responseWrapper.resetBuffer();
                responseWrapper.getOutputStream().write(rawResponseBody.getBytes());
            }
            responseWrapper.copyBodyToResponse();
            TraceIdUtils.clear();
        }

    }

    // 判断是否为 multipart 请求
    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().toLowerCase().startsWith("multipart/");
    }

    // 解析 multipart 请求内容, 文件字段记录为元信息 (不读文件内容)
    private String buildMultipartJson(HttpServletRequest request)
            throws IOException, ServletException {

        JSONObject root = new JSONObject();

        // 普通表单字段
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length == 1) {
                root.put(k, v[0]);
            } else {
                root.put(k, v);
            }
        });

        // 文件字段
        Collection<Part> parts = request.getParts();

        // 用 Map 收集同名文件
        Map<String, JSONArray> fileMap = new HashMap<>();

        for (Part part : parts) {
            if (part.getContentType() != null) {
                JSONObject fileNode = new JSONObject();
                fileNode.put("fileName", part.getSubmittedFileName());
                fileNode.put("size", part.getSize());
                fileNode.put("contentType", part.getContentType());

                // 如果是同名文件，就放到数组里
                fileMap.computeIfAbsent(part.getName(), k -> new JSONArray())
                        .add(fileNode);
            }
        }

        // 放回 root
        root.putAll(fileMap);

        return JSON.toJSONString(root);
    }

    public boolean isFileResponse(HttpServletResponse response) {

        String contentType = response.getContentType();
        String contentDisposition = response.getHeader("Content-Disposition");

        if (contentType == null)
            return contentDisposition != null && contentDisposition.toLowerCase().contains("attachment");

        contentType = contentType.toLowerCase();

        // 常见文件类型
        List<String> fileTypes = Arrays.asList(
                "application/octet-stream",      // 通用二进制流
                "application/pdf",               // PDF
                "application/zip",               // ZIP
                "application/x-rar-compressed", // RAR
                "application/x-7z-compressed",  // 7z
                "application/msword",            // Word 旧版
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // Word 新版
                "application/vnd.ms-excel",      // Excel 旧版
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // Excel 新版
                "application/vnd.ms-powerpoint", // PPT 旧版
                "application/vnd.openxmlformats-officedocument.presentationml.presentation", // PPT 新版
                "application/x-msdownload",      // Windows 可执行文件
                "application/java-archive",      // Jar 文件
                "application/x-shockwave-flash", // Flash
                "image/",                        // 各种图片
                "audio/",                        // 各种音频
                "video/"                         // 各种视频
        );

        // 判断 contentType 是否匹配任何文件类型
        boolean isFileType = fileTypes.stream().anyMatch(contentType::startsWith);

        // 如果 Content-Disposition 存在 attachment，也认为是文件
        boolean isAttachment = contentDisposition != null && contentDisposition.toLowerCase().contains("attachment");

        return isFileType || isAttachment;
    }

    /**
     * 获取文件名
     */
    public String getFileNameFromResponse(HttpServletResponse response) {
        String disposition = response.getHeader("Content-Disposition");
        String filename = "未知文件名";
        if (disposition == null || disposition.isEmpty()) return filename;

        // 1) 优先解析 filename* (RFC5987)
        Matcher mStar = Pattern.compile("filename\\*=UTF-8''([^;\\r\\n]+)", Pattern.CASE_INSENSITIVE)
                .matcher(disposition);
        if (mStar.find()) {
            String encoded = mStar.group(1);
            try {
                return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
            } catch (Exception ignored) {
            }
        }

        // 2) 再解析普通 filename=
        Matcher m = Pattern.compile("(?i)(?<!\\*)filename=\"?([^\";]+)\"?").matcher(disposition);
        if (m.find()) {
            String raw = m.group(1).trim();

            // 检测 RFC2047: =?UTF-8?Q?...?=
            if (raw.matches("=\\?UTF-8\\?[qQbB]\\?.+\\?=")) {
                return decodeRFC2047(raw);
            }

            return raw;
        }

        return filename;
    }

    // RFC2047 解码函数
    private String decodeRFC2047(String text) {
        try {
            return MimeUtility.decodeText(text);
        } catch (Exception e) {
            return text;
        }
    }

}
