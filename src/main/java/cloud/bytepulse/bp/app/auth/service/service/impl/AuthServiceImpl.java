package cloud.bytepulse.bp.app.auth.service.service.impl;


import cloud.bytepulse.bp.app.auth.dto.LoginDTO;
import cloud.bytepulse.bp.app.auth.service.service.AuthService;
import cloud.bytepulse.bp.app.auth.vo.auth.LoginResultVO;
import cloud.bytepulse.bp.common.util.JWTUtils;
import cloud.bytepulse.bp.common.util.RedisUtils;
import cloud.bytepulse.bp.common.util.ReqUtils;
import cloud.bytepulse.bp.domain.entity.SysUser;
import cloud.bytepulse.bp.domain.mapper.SysUserMapper;
import cloud.bytepulse.bp.domain.models.auth.pojo.LoginUser;
import cloud.bytepulse.bp.domain.models.auth.pojo.LoginUserInfo;
import cloud.bytepulse.bp.framework.exception.BytePulseException;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cloud.bytepulse.bp.domain.models.auth.pojo.LoginUser.NEED_RE_LOGIN;

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

    /**
     * 获取验证码
     */
    @Override
    public Map<String, String> captcha() throws IOException {
        Map<String, String> map = new HashMap<>();

        GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(160, 60, 4);
        String imageBase64Data = gifCaptcha.getImageBase64Data();
        map.put("captcha", imageBase64Data);
        String uid = UUID.randomUUID().toString().replaceAll("-", "");
        map.put("uid", uid);

        // 结果存入redis
        redisUtils.setCacheObject("captcha:" + uid, gifCaptcha.getCode(), 30L, TimeUnit.SECONDS);
        return map;
    }

    /**
     * 登录
     */
    @Override
    public LoginResultVO login(LoginDTO loginDTO) {
        // 验证码
        String captcha = redisUtils.getCacheObject("captcha:" + loginDTO.getUid());
        redisUtils.deleteObject("captcha:" + loginDTO.getUid());
        if (captcha == null || !captcha.equalsIgnoreCase(loginDTO.getCaptcha())) {
            throw new BytePulseException("验证码错误");
        }
        // 使用authenticate进行认证
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authentication);
        // 认证没通过,给出提示
        if (authenticate == null) {
            throw new BytePulseException("登陆失败");
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
        loginUserInfo.setSessionId(fingerprint);
        // 使用 userId 和 角色 生成token,返回token
        String userId = String.valueOf(loginUser.getLoginUserInfo().getUserId());
        JWTUtils.Payload payload = new JWTUtils.Payload();
        payload.with("userId", userId).with("fingerprint", fingerprint);
        String token = JWTUtils.createToken(payload, 60 * 60 * 24 * 7);
        // 把用户信息存入redis
        redisUtils.setCacheObject("login:" + userId, loginUser, 3600L * 60L, TimeUnit.SECONDS);
        // 返回token给前端
        loginResultVO.setToken(token);
        // 移除需要重新登录的标记
        NEED_RE_LOGIN.remove(Integer.valueOf(userId));
        return loginResultVO;
    }
}
