package cloud.bytepulse.bp.app.auth.service.impl;


import cloud.bytepulse.bp.app.auth.authentication.token.WechatAuthenticationToken;
import cloud.bytepulse.bp.app.auth.service.AuthService;
import cloud.bytepulse.bp.app.auth.vo.auth.LoginResultVO;
import cloud.bytepulse.bp.common.enums.errorcode.AuthErrorCode;
import cloud.bytepulse.bp.common.util.JWTUtils;
import cloud.bytepulse.bp.common.util.RedisUtils;
import cloud.bytepulse.bp.common.util.ReqUtils;
import cloud.bytepulse.bp.domain.entity.SysUser;
import cloud.bytepulse.bp.domain.mapper.SysUserMapper;
import cloud.bytepulse.bp.domain.models.auth.LoginUser;
import cloud.bytepulse.bp.domain.models.auth.LoginUserInfo;
import cloud.bytepulse.bp.framework.exception.BytePulseException;
import cloud.bytepulse.bp.core.properties.AppProperties;
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

import static cloud.bytepulse.bp.domain.models.auth.LoginUser.NEED_RE_LOGIN;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final RedisUtils redisUtils;

    private final SysUserMapper sysUserMapper;

    private final AppProperties appProperties;

    /**
     * 获取验证码
     */
    @Override
    public Map<String, String> captcha() {
        Map<String, String> map = new HashMap<>();

        AppProperties.Captcha captchaConfig = appProperties.getCaptcha();

        GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(captchaConfig.getWidth(),
                captchaConfig.getHeight(),
                captchaConfig.getLength());
        String imageBase64Data = gifCaptcha.getImageBase64Data();
        map.put("captcha", imageBase64Data);
        String uid = UUID.randomUUID().toString().replace("-", "");
        map.put("uid", uid);

        // 结果存入redis
        redisUtils.setCacheObject("captcha:" + uid, gifCaptcha.getCode(), captchaConfig.getExpirationSeconds(), TimeUnit.SECONDS);
        return map;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String uid, String captcha) {
        // 验证码
        String cacheCaptcha = redisUtils.getCacheObject("captcha:" + uid);
        redisUtils.deleteObject("captcha:" + uid);
        if (cacheCaptcha == null || !cacheCaptcha.equalsIgnoreCase(captcha)) {
            throw new BytePulseException(AuthErrorCode.CAPTCHA_ERROR);
        }
    }

    /**
     * 登录返回 token
     */
    public LoginResultVO createAuthToken(Authentication authenticate) {
        // 认证没通过,给出提示
        if (authenticate == null) {
            throw new BytePulseException(AuthErrorCode.LOGIN_FAIL);
        }
        // 认证通过
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        LoginUserInfo loginUserInfo = loginUser.getLoginUserInfo();
        // 返回给前端的数据
        LoginResultVO loginResultVO = new LoginResultVO();
        loginResultVO.setUserId(loginUserInfo.getUserId());
        loginResultVO.setNickname(loginUserInfo.getNickname());
        loginResultVO.setUsername(loginUserInfo.getUsername());
        loginResultVO.setLastLogin(loginUserInfo.getLastLogin());
        loginResultVO.setLastLoginIp(loginUserInfo.getLastLoginIp());
        // 更新登录记录
        SysUser user = new SysUser();
        user.setId(loginUserInfo.getUserId());
        user.setLastLoginIp(ReqUtils.getIP());
        user.setLastLogin(new Date());
        sysUserMapper.updateById(user);

        // 生成UUID作为token指纹
        String fingerprint = UUID.randomUUID().toString();
        loginUserInfo.setFingerprint(fingerprint);
        // 使用 userId 和 角色 生成token,返回token
        String userId = String.valueOf(loginUser.getLoginUserInfo().getUserId());
        JWTUtils.Payload payload = new JWTUtils.Payload();
        payload.with("userId", userId).with("fingerprint", fingerprint);
        String token = JWTUtils.createToken(payload);
        // 把用户信息存入redis
        if (appProperties.getLogin().getExpirationMinutes() == 0) {
            redisUtils.setCacheObject("login:" + userId, loginUser);
        } else {
            redisUtils.setCacheObject("login:" + userId, loginUser,
                    appProperties.getLogin().getExpirationMinutes(), TimeUnit.MINUTES);
        }
        // 返回token给前端
        loginResultVO.setToken(token);
        // 移除需要重新登录的标记
        NEED_RE_LOGIN.remove(loginUser.getLoginUserInfo().getUserId());
        return loginResultVO;
    }

    @Override
    public LoginResultVO login(String username, String password) {
        // 使用authenticate进行认证
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authentication);
        return createAuthToken(authenticate);
    }

    /**
     * 微信登录
     */
    @Override
    public LoginResultVO weChatLogin(String code) {
        // TODO 通过 code 调用微信接口获取 openid
        String openId = code;
        // 使用authenticate进行认证
        Authentication authentication =
                new WechatAuthenticationToken(openId);
        Authentication authenticate = authenticationManager.authenticate(authentication);
        return createAuthToken(authenticate);
    }
}
