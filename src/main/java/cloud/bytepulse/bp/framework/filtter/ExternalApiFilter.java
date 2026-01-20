package cloud.bytepulse.bp.framework.filtter;

import cloud.bytepulse.bp.common.utils.CryptoUtils;
import cloud.bytepulse.bp.common.utils.RedisUtils;
import cloud.bytepulse.bp.common.utils.ReqUtils;
import cloud.bytepulse.bp.domain.entity.ApiCredentials;
import cloud.bytepulse.bp.domain.mapper.ApiCredentialsMapper;
import cloud.bytepulse.bp.framework.constant.ExternalApiConstant;
import cloud.bytepulse.bp.framework.http.wrapper.CachedBodyRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * 第三方接口 认证过滤器
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
@Component
@RequiredArgsConstructor
public class ExternalApiFilter extends OncePerRequestFilter {

    private final ApiCredentialsMapper apiCredentialsMapper;

    private final RedisUtils redisUtils;

    @Value("${api.external.secretKey}")
    private String secretKey;

    @Value("${api.external.iv}")
    private String iv;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("ExternalApiFilter");
        response.setCharacterEncoding("utf-8");
        String requestURI = request.getRequestURI();
        // 不是第三方接口，直接放行
        if (!ReqUtils.isPathMatching(ExternalApiConstant.EXTERNAL_API, requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        CachedBodyRequestWrapper cachedRequest = new CachedBodyRequestWrapper(request);
        String appKey = request.getHeader("X-App-Key");
        String timestamp = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");
        String signature = request.getHeader("X-Signature");
        if (appKey == null || timestamp == null || nonce == null || signature == null) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        String hashAppKey = null;
        try {
            hashAppKey = CryptoUtils.getSHA256(appKey);
        } catch (Exception e) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        // 查询 appKey
        ApiCredentials apiCredentials = apiCredentialsMapper.selectByApiKeyHash(hashAppKey);
        if (apiCredentials == null) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        // 判断时间是否超多5分钟
        long timestampLong = Long.parseLong(timestamp);
        if (System.currentTimeMillis() - timestampLong > 5 * 60 * 1000) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        String redisNonce = redisUtils.getCacheObject("nonce:" + appKey + ":" + nonce);
        // 判断 nonce 是否重复
        if ("1".equals(redisNonce)) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        // 缓存 nonce
        redisUtils.setCacheObject("nonce:" + appKey + ":" + nonce, "1", 300L, TimeUnit.SECONDS);
        String cachedBody = cachedRequest.getCachedBodyAsString();
        // 校验签名
        // 获取用户的密钥
        String userSecret = null;
        try {
            userSecret = CryptoUtils.decryptWithAES(apiCredentials.getApiSecretEnc(),
                    secretKey,
                    CryptoUtils.base64ToIV(iv));
        } catch (Exception e) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(requestURI).append("\n")
                    .append(appKey).append("\n")
                    .append(timestamp).append("\n")
                    .append(nonce).append("\n")
                    .append(CryptoUtils.getSHA256(cachedBody));
        } catch (Exception e) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        // 签名校验
        String sign = null;
        try {
            sign = CryptoUtils.signHmacSHA256Hex(stringBuilder.toString(), userSecret);
        } catch (Exception e) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        if (!MessageDigest.isEqual(
                sign.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8))) {
            Utils.printUnauthorized(response, "未授权");
            return;
        }
        filterChain.doFilter(cachedRequest, response);
    }
}