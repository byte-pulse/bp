package cloud.bytepulse.bp.framework.filtter;

import cloud.bytepulse.bp.common.utils.JWTUtils;
import cloud.bytepulse.bp.common.utils.JsonUtils;
import cloud.bytepulse.bp.common.utils.RedisUtils;
import cloud.bytepulse.bp.common.utils.ReqUtils;
import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.domain.models.auth.domain.LoginUser;
import cloud.bytepulse.bp.framework.constant.AllHandlerConstant;
import cloud.bytepulse.bp.framework.constant.AnonymousConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static cloud.bytepulse.bp.common.utils.ReqUtils.isPathMatching;
import static cloud.bytepulse.bp.domain.models.auth.domain.LoginUser.NEED_RE_LOGIN;

/**
 * token认证过滤器
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        // 获取token
        String token = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        // 匿名接口和不存在的接口直接放行
        if (!ReqUtils.isPathMatching(AllHandlerConstant.ALL_HANDLER, requestURI)) {
            printNotFound(request, response);
            return;
        }
        if (isPathMatching(AnonymousConstant.ANONYMOUS, requestURI)) {
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
            printUnauthorized(response, "未登录");
            return;
        }
        // 从redis中获取用户信息
        LoginUser loginUser = redisUtils.getCacheObject("login:" + userId);
        if (loginUser == null || loginUser.getLoginUserInfo() == null) {
            printUnauthorized(response, "登录状态失效");
            return;
        }
        String sessionId = loginUser.getLoginUserInfo().getSessionId();
        if (fingerprint == null || !fingerprint.equals(sessionId)) {
            printUnauthorized(response, "登录过期");
            return;
        }
        if (NEED_RE_LOGIN.containsKey(Integer.valueOf(userId))) {
            NEED_RE_LOGIN.remove(Integer.valueOf(userId));
            printUnauthorized(response, "账户或角色有调整，请重新登录");
            return;
        }
        ReqUtils.getRequest().setAttribute("userId", userId);
        // 把用户信息重新入redis
        redisUtils.setCacheObject("login:" + userId, loginUser, 3600L * 60L, TimeUnit.SECONDS);
        // 将用户信息存入SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    /**
     * 401 输出
     */
    private void printUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JsonUtils.toJsonStr(ApiResponse.unauthorized(msg)));
    }

    /**
     * 404 输出
     *
     */
    private void printNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JsonUtils.toJsonStr(
                ApiResponse.notFound()
                        .put("message", "资源不存在:" + request.getRequestURI())));
    }
}