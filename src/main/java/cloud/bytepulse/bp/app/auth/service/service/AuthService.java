package cloud.bytepulse.bp.app.auth.service.service;


import cloud.bytepulse.bp.app.auth.dto.auth.LoginDTO;
import cloud.bytepulse.bp.domain.ApiResponse;

import java.io.IOException;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
public interface AuthService {


    /**
     * 获取验证码
     */
    ApiResponse captcha() throws IOException;

    /**
     * 登录
     */
    ApiResponse login(LoginDTO loginDTO);

    /**
     * 检查登陆状态
     */
    ApiResponse check();
}
