package cloud.bytepulse.service.auth.impl;

import cloud.bytepulse.cache.redis.RedisCache;
import cloud.bytepulse.common.core.exception.BytePulseException;
import cloud.bytepulse.common.core.exception.enums.AuthErrorCode;
import cloud.bytepulse.common.core.properties.AppProperties;
import cloud.bytepulse.common.core.util.JWTUtils;
import cloud.bytepulse.common.core.util.ReqUtils;
import cloud.bytepulse.data.entity.SysUser;
import cloud.bytepulse.data.mapper.SysUserMapper;
import cloud.bytepulse.security.LoginUser;
import cloud.bytepulse.security.LoginUserInfo;
import cloud.bytepulse.service.auth.AuthService;
import cloud.bytepulse.service.auth.vo.LoginResultVO;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cloud.bytepulse.security.Auths.NEED_RE_LOGIN;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:54
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final RedisCache redisCache;

    private final SysUserMapper sysUserMapper;

    private final AppProperties appProperties;

    /**
     * 获取验证码
     */
    @Override
    public Map<String, String> captcha() {
        Map<String, String> map = new HashMap<>();

        AppProperties.CaptchaProperties captchaConfig = appProperties.getCaptcha();

        GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(captchaConfig.getWidth(),
                captchaConfig.getHeight(),
                captchaConfig.getLength());
        String imageBase64Data = gifCaptcha.getImageBase64Data();
        map.put("captcha", imageBase64Data);
        String uid = UUID.randomUUID().toString().replace("-", "");
        map.put("uid", uid);

        // 结果存入redis
        redisCache.setCacheObject("captcha:" + uid, gifCaptcha.getCode(), captchaConfig.getExpirationSeconds(), TimeUnit.SECONDS);
        return map;
    }
}
