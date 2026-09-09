package cloud.bytepulse.web.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 支持多次读取的HttpServletRequest包装类
 * 1. 支持body内容多次读取
 * 2. 不影响Spring的文件上传功能
 * 3. 支持请求体修改
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
@Getter
public class LoggingCachedBodyRequestWrapper extends ContentCachingRequestWrapper {

    /**
     * -- GETTER --
     * 是否为文件上传请求
     */
    private final boolean isMultipartRequest;

    public LoggingCachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request, 1024);
        // 检查是否为文件上传请求
        String contentType = request.getContentType();
        isMultipartRequest = contentType != null && contentType.toLowerCase().startsWith("multipart/");

    }

}
