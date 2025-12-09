package cloud.bytepulse.bp.app.auth.controller;

import cloud.bytepulse.bp.app.auth.dto.auth.LoginDTO;
import cloud.bytepulse.bp.app.auth.service.service.AuthService;
import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.framwork.annotation.Anonymous;
import cloud.bytepulse.bp.framwork.annotation.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 认证
 *
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
@Tag(name = "管理-登录认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    @Log(value = "管理-获取验证码")
    @Anonymous
    public ApiResponse captcha() throws IOException {
        return authService.captcha();
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @Log(value = "管理-用户登录", persist = true)
    @Anonymous
    public ApiResponse login(@RequestBody @Validated LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @GetMapping("/check")
    @Operation(summary = "检查登陆状态")
    @Log(value = "管理-检查登陆状态")
    public ApiResponse check() {
        return authService.check();
    }
}
