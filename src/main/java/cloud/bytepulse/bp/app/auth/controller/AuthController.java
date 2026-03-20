package cloud.bytepulse.bp.app.auth.controller;

import cloud.bytepulse.bp.app.auth.dto.LoginDTO;
import cloud.bytepulse.bp.app.auth.service.AuthService;
import cloud.bytepulse.bp.app.auth.vo.auth.LoginResultVO;
import cloud.bytepulse.bp.common.annotation.Anonymous;
import cloud.bytepulse.bp.common.annotation.NoLogging;
import cloud.bytepulse.bp.common.annotation.RequestLimit;
import cloud.bytepulse.bp.common.util.RedisUtils;
import cloud.bytepulse.bp.domain.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    private final RedisUtils redisUtils;

    @GetMapping("/captcha")
    @Operation(summary = "获取验证码")
    @Anonymous
    @NoLogging
    @RequestLimit(count = 10, time = 8000)
    public ApiResponse<Map<String, String>> captcha() throws IOException {
        Map<String, String> map = authService.captcha();
        return ApiResponse.success(map);
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @Anonymous
    public ApiResponse<LoginResultVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        LoginResultVO loginResultVO = authService.login(loginDTO);
        return ApiResponse.success(loginResultVO);
    }

    @PostMapping("/getToken")
    @Operation(summary = "无验证码直接登陆")
    @Anonymous
    public ApiResponse<LoginResultVO> login(@RequestParam String username, @RequestParam String password) throws IOException {
        Map<String, String> map = authService.captcha();
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(username);
        loginDTO.setPassword(password);
        String uid = map.get("uid");
        loginDTO.setUid(uid);
        String captcha = redisUtils.getCacheObject("captcha:" + loginDTO.getUid());
        loginDTO.setCaptcha(captcha);
        LoginResultVO loginResultVO = authService.login(loginDTO);
        return ApiResponse.success(loginResultVO);
    }

    @GetMapping("/check")
    @Operation(summary = "检查登陆状态")
    @NoLogging
    public ApiResponse<Void> check() {
        return ApiResponse.success();
    }
}
