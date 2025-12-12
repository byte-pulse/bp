package cloud.bytepulse.bp.app.auth.service.service;


import cloud.bytepulse.bp.app.auth.dto.auth.LoginDTO;
import cloud.bytepulse.bp.app.auth.vo.auth.LoginResultVO;

import java.io.IOException;
import java.util.Map;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
public interface AuthService {


    /**
     * 获取验证码
     */
    Map<String, String> captcha() throws IOException;

    /**
     * 登录
     */
    LoginResultVO login(LoginDTO loginDTO);
}
