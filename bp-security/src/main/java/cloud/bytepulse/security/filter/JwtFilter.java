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
        String userId = null;
        String fingerprint = null;
        try {
            userId = JWTUtils.parseToken(token, "userId", false);
            fingerprint = JWTUtils.parseToken(token, "fingerprint", false);
        } catch (Exception e) {
            ApiResponse.printUnauthorized(response, "未登录");
            return;
        }
        // 从redis中获取用户信息
        LoginUser loginUser = redisCache.getCacheObject("login:" + userId, LoginUser.class);
        if (loginUser == null || loginUser.getLoginUserInfo() == null) {
            ApiResponse.printUnauthorized(response, "登录状态失效");
            return;
        }
        String redisFingerprint = loginUser.getLoginUserInfo().getFingerprint();
        if (fingerprint == null) {
            ApiResponse.printUnauthorized(response, "登录过期");
            return;
        } else if (!fingerprint.equals(redisFingerprint)) {
            ApiResponse.printUnauthorized(response, "账号在别处登陆");
            return;
        }
        if (NEED_RE_LOGIN.contains(Long.valueOf(userId))) {
            NEED_RE_LOGIN.remove(Long.valueOf(userId));
            ApiResponse.printUnauthorized(response, "账户或角色有调整，请重新登录");
            return;
        }
        ReqUtils.getRequest().setAttribute("userId", userId);
        // 把用户信息重新入redis
        if (appProperties.getLogin().getExpirationMinutes() == 0) {
            redisCache.setCacheObject("login:" + userId, loginUser);
        } else {
            redisCache.setCacheObject("login:" + userId, loginUser,
                    appProperties.getLogin().getExpirationMinutes(), TimeUnit.MINUTES);
        }
        // 将用户信息存入SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
