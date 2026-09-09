package cloud.bytepulse.service.auth;

import cloud.bytepulse.service.auth.vo.LoginResultVO;

import java.util.Map;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:53
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
}
