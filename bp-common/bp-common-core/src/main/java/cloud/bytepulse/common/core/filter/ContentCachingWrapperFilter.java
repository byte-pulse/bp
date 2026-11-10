package cloud.bytepulse.common.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-10 14:12
 */
public class ContentCachingWrapperFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // SSE 等流式响应不能走缓存包装：请求线程结束时 copyBodyToResponse 会提前提交响应
        // 并把 Content-Length 固化为当前已缓存的数据量，导致后续消息无法下发
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        return accept != null && accept.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. 将原始请求和响应包装成可缓存的版本
        // 注意：建议设置缓存大小限制，防止内存溢出
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            // 2. 将包装后的对象传递下去
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 3. 确保将缓存的响应内容写回到原始响应中
            //    如果不调用此方法，响应内容将无法返回给客户端
            responseWrapper.copyBodyToResponse();
        }
    }
}
