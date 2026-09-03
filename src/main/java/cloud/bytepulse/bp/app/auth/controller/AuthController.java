package cloud.bytepulse.bp.app.auth.controller;

import cloud.bytepulse.bp.app.auth.dto.LoginDTO;
import cloud.bytepulse.bp.app.auth.service.AuthService;
import cloud.bytepulse.bp.app.auth.vo.auth.LoginResultVO;
import cloud.bytepulse.bp.common.annotation.Anonymous;
import cloud.bytepulse.bp.common.annotation.NoLogging;
import cloud.bytepulse.bp.common.annotation.RequestLimit;
import cloud.bytepulse.bp.common.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证
 *
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
@Tag(name = "登录认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    @Anonymous
    @NoLogging
    @RequestLimit(count = 10, time = 8000)
    public ApiResponse<Map<String, String>> captcha() {
        Map<String, String> map = authService.captcha();
        return ApiResponse.success(map);
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @Anonymous
    public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        // 先校验验证码
        authService.checkCaptcha(loginDTO.getUid(), loginDTO.getCaptcha());
        LoginResultVO loginResultVO = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return ApiResponse.success(loginResultVO);
    }

    /**
     * 此接口仅用于开发环境测试，生产环境不应该使用
     */
    @Profile({"dev", "test"})
    @PostMapping("/getToken")
    @Operation(summary = "无验证码直接登陆")
    @Anonymous
    public ApiResponse<LoginResultVO> login(@RequestParam String username, @RequestParam String password) {
        LoginResultVO loginResultVO = authService.login(username, password);
        return ApiResponse.success(loginResultVO);
    }

    @PostMapping("/weChatLogin")
    @Operation(summary = "微信登录")
    @Anonymous
    public ApiResponse<LoginResultVO> weChatLogin(@RequestParam String code) {
        LoginResultVO loginResultVO = authService.weChatLogin(code);
        return ApiResponse.success(loginResultVO);
    }

    @GetMapping("/check")
    @Operation(summary = "检查登陆状态")
    @NoLogging
    public ApiResponse<Void> check() {
        return ApiResponse.success();
    }
}
