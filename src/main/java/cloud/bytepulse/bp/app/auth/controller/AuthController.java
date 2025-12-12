package cloud.bytepulse.bp.app.auth.controller;

import cloud.bytepulse.bp.app.auth.dto.auth.LoginDTO;
import cloud.bytepulse.bp.app.auth.service.service.AuthService;
import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.framework.annotation.Anonymous;
import cloud.bytepulse.bp.framework.annotation.NoLogging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    @Anonymous
    @NoLogging
    public ApiResponse captcha() throws IOException {
        return authService.captcha();
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    @Anonymous
    public ApiResponse login(@RequestBody @Validated LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @GetMapping("/check")
    @Operation(summary = "检查登陆状态")
    @NoLogging
    public ApiResponse check() {
        return authService.check();
    }

    @PostMapping("/fileUploadDownloadTest")
    @Operation(summary = "测试文件上传下载")
    @Anonymous
    public ResponseEntity<byte[]> check1(@RequestParam String n, @RequestParam MultipartFile[] f) throws IOException {
        // 获取文件内容
        byte[] fileBytes = f[0].getBytes();
        // 获取原文件名
        String filename = f[0].getOriginalFilename();

        // 设置返回的 HTTP 头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", f[0].getContentType());
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build());

        // 返回文件内容
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
