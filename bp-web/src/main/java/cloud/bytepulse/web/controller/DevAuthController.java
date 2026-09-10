package cloud.bytepulse.web.controller;


import cloud.bytepulse.common.core.annotation.Anonymous;
import cloud.bytepulse.common.core.annotation.BpLogging;
import cloud.bytepulse.common.core.enums.OperateEnum;
import cloud.bytepulse.common.core.model.ApiResponse;
import cloud.bytepulse.service.auth.AuthService;
import cloud.bytepulse.service.auth.vo.LoginResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证
 *
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
@Tag(name = "登录认证")
@Profile({"dev", "test"})
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class DevAuthController {

    private final AuthService authService;

    /**
     * 此接口仅用于开发环境测试，生产环境不应该使用
     */
    @PostMapping("/getToken")
    @Operation(summary = "无验证码直接登陆")
    @Anonymous
    @BpLogging(value = OperateEnum.LOGIN, desc = "无验证码直接登陆")
    public ApiResponse<LoginResultVO> login(@RequestBody LoginParam loginParam) {
        LoginResultVO loginResultVO = authService.login(loginParam.username, loginParam.password);
        return ApiResponse.success(loginResultVO);
    }

    @Data
    public static class LoginParam {
        private String username;
        private String password;
    }
}
