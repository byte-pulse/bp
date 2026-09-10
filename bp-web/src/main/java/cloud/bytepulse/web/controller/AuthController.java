package cloud.bytepulse.web.controller;


import cloud.bytepulse.common.core.annotation.Anonymous;
import cloud.bytepulse.common.core.annotation.BpLogging;
import cloud.bytepulse.common.core.annotation.RequestLimit;
import cloud.bytepulse.common.core.enums.OperateEnum;
import cloud.bytepulse.common.core.model.ApiResponse;
import cloud.bytepulse.service.auth.AuthService;
import cloud.bytepulse.service.auth.dto.LoginDTO;
import cloud.bytepulse.service.auth.vo.LoginResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @RequestLimit(count = 10, time = 8000)
    public ApiResponse<Map<String, String>> captcha() {
        Map<String, String> map = authService.captcha();
        return ApiResponse.success(map);
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @Anonymous
    @BpLogging(value = OperateEnum.LOGIN, desc = "登录")
    public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        // 先校验验证码
        authService.checkCaptcha(loginDTO.getUid(), loginDTO.getCaptcha());
        LoginResultVO loginResultVO = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return ApiResponse.success(loginResultVO);
    }

    @GetMapping("/check")
    @Operation(summary = "检查登陆状态")
    @PreAuthorize("hasRole('admin')")
    @BpLogging(value = OperateEnum.QUERY, desc = "检查登陆状态")
    public ApiResponse<Void> check() {
        return ApiResponse.success();
    }
}
