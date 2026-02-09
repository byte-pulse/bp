package cloud.bytepulse.security.filter;

import cloud.bytepulse.cache.redis.RedisCache;
import cloud.bytepulse.common.core.constant.ApiPathRegistry;
import cloud.bytepulse.common.core.model.ApiResponse;
import cloud.bytepulse.common.core.properties.AppProperties;
import cloud.bytepulse.common.core.util.JWTUtils;
import cloud.bytepulse.common.core.util.ReqUtils;
import cloud.bytepulse.security.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static cloud.bytepulse.security.Auths.NEED_RE_LOGIN;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 16:33
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final RedisCache redisCache;

    private final AppProperties appProperties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 获取token
        String token = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        String method = request.getMethod();

        // 匿名接口直接放行
        if (ReqUtils.isPathMatching(ApiPathRegistry.ANONYMOUS_API.get(method), requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 解析token,获取userId 和 指纹
}
