package cloud.bytepulse.bp.framework.filtter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * token认证过滤器
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
@Component
public class GlobalCorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("GlobalCorsFilter");

        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin")); // 允许前端请求的来源
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // 允许的请求方法
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Auth"); // 允许的请求头
        response.setHeader("Access-Control-Allow-Credentials", "true"); // 允许携带 Cookie 或认证信息
        response.setHeader("Access-Control-Max-Age", "3600"); // 预检请求缓存时间

        // 处理预检请求，直接返回 204 状态码
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        filterChain.doFilter(request, response);
    }
}