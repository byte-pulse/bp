package cloud.bytepulse.bp.app.auth.service;


import cloud.bytepulse.bp.app.auth.vo.auth.LoginResultVO;

import java.util.Map;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
public interface AuthService {

    /**
     * 获取验证码
     */
    Map<String, String> captcha();

    /**
     * 校验验证码
     */
    void checkCaptcha(String uid, String captcha);

    /**
     * 用户名密码登录
     */
    LoginResultVO login(String username, String password);

    /**
     * 微信登录
     */
    LoginResultVO weChatLogin(String openId);
}
