package cloud.bytepulse.bp.framework.wrapper;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.util.mime.MimeUtility;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 支持多次读取的HttpServletResponse包装类
 * 1. 支持响应体内容多次读取
 * 2. 支持响应体修改
 * 3. 不影响文件下载功能
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
public class LoggingCachedBodyResponseWrapper extends ContentCachingResponseWrapper {


    public LoggingCachedBodyResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public boolean isFileResponse() {

        String contentType = getContentType();
        String contentDisposition = getHeader("Content-Disposition");

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
    public String getFileNameFromResponse() {
        String disposition = getHeader("Content-Disposition");
        String filename = "未知文件名";
        if (disposition == null || disposition.isEmpty()) return filename;

        // 1) 优先解析 filename* (RFC5987)
        Matcher mStar = Pattern.compile("filename\\*=UTF-8''([^;\\r\\n]+)", Pattern.CASE_INSENSITIVE)
                .matcher(disposition);
        if (mStar.find()) {
            String encoded = mStar.group(1);
            try {
                return URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
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
