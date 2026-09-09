package cloud.bytepulse.common.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 13:15
 */
@Slf4j
public class SimpleEncodingFilter extends OncePerRequestFilter {

    // 需要设置编码的Content-Type列表
    private static final List<String> TEXT_CONTENT_TYPES = Arrays.asList(
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.TEXT_HTML_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_XML_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    );


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 设置编码为 UTF-8
        String requestContentType = request.getContentType();
        if (isTextContent(requestContentType)) {
            // 只对文本类型设置请求编码
            if (!StringUtils.hasText(request.getCharacterEncoding())) {
                request.setCharacterEncoding(StandardCharsets.UTF_8);
            }
        }
        filterChain.doFilter(request, response);
        String responseContentType = response.getContentType();
        if (isTextContent(responseContentType)) {
            // 只对文本类型设置响应编码
            if (!StringUtils.hasText(response.getCharacterEncoding())) {
                response.setCharacterEncoding(StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 判断是否是文本内容
     */
    private boolean isTextContent(String contentType) {
        if (contentType == null) {
            return false;
        }

        // 获取主类型（去除参数）
        String mimeType = contentType.split(";")[0].trim().toLowerCase();

        // 检查是否在文本类型列表中
        return TEXT_CONTENT_TYPES.contains(mimeType);
    }
}
