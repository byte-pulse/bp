package cloud.bytepulse.bp.framework.filter;

import cloud.bytepulse.bp.common.constant.ControllerApiConstant;
import cloud.bytepulse.bp.common.util.JWTUtils;
import cloud.bytepulse.bp.common.util.RedisUtils;
import cloud.bytepulse.bp.common.util.ReqUtils;
import cloud.bytepulse.bp.domain.models.auth.pojo.LoginUser;
import cloud.bytepulse.bp.framework.properties.AppProperties;
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

import static cloud.bytepulse.bp.common.util.ReqUtils.isPathMatching;
import static cloud.bytepulse.bp.domain.models.auth.pojo.LoginUser.NEED_RE_LOGIN;

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

    private final AppProperties appProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        // 获取token
        String token = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        // 匿名接口和不存在的接口直接放行
        // 提供给第三方的接口也放行
        if (!ReqUtils.isPathMatching(ControllerApiConstant.ALL_API, requestURI)) {
            Utils.printNotFound(request, response);
            return;
        }
        if (isPathMatching(ControllerApiConstant.ANONYMOUS_API, requestURI)
                || isPathMatching(ControllerApiConstant.EXTERNAL_API, requestURI)) {
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
            Utils.printUnauthorized(response, "未登录");
            return;
        }
        // 从redis中获取用户信息
        LoginUser loginUser = redisUtils.getCacheObject("login:" + userId);
        if (loginUser == null || loginUser.getLoginUserInfo() == null) {
            Utils.printUnauthorized(response, "登录状态失效");
            return;
        }
        String redisFingerprint = loginUser.getLoginUserInfo().getFingerprint();
        if (fingerprint == null) {
            Utils.printUnauthorized(response, "登录过期");
            return;
        } else if (!fingerprint.equals(redisFingerprint)) {
            Utils.printUnauthorized(response, "账号在别处登陆");
            return;
        }
        if (NEED_RE_LOGIN.contains(Long.valueOf(userId))) {
            NEED_RE_LOGIN.remove(Long.valueOf(userId));
            Utils.printUnauthorized(response, "账户或角色有调整，请重新登录");
            return;
        }
        ReqUtils.getRequest().setAttribute("userId", userId);
        // 把用户信息重新入redis
        if (appProperties.getLogin().getExpirationMinutes() == 0) {
            redisUtils.setCacheObject("login:" + userId, loginUser);
        } else {
            redisUtils.setCacheObject("login:" + userId, loginUser,
                    appProperties.getLogin().getExpirationMinutes(), TimeUnit.MINUTES);
        }
        // 将用户信息存入SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}