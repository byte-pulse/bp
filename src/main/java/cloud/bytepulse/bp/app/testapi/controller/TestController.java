package cloud.bytepulse.bp.app.testapi.controller;

import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.framework.annotation.Anonymous;
import cloud.bytepulse.bp.framework.annotation.ExternalApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author jiejiebiezheyang
 * @since 2025-12-12 15:30
 */
@Tag(name = "测试接口")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    @PostMapping("/fileUploadDownload")
    @Operation(summary = "测试文件上传下载")
    @Anonymous
    public ResponseEntity<byte[]> fileUploadDownload(@RequestParam String n, @RequestParam MultipartFile[] f) throws IOException {
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

    @PostMapping(value = "/external")
    @Operation(summary = "测试外部接口")
    @ExternalApi
    public ApiResponse testExternalApi(ExternalDTO externalDTO) {
        return ApiResponse.success(externalDTO);
    }

    @Data
    public static class ExternalDTO {
        private String orderId;
        private Double amount;
    }
}
