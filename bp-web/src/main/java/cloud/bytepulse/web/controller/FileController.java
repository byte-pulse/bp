package cloud.bytepulse.web.controller;

import cloud.bytepulse.common.core.annotation.Anonymous;
import cloud.bytepulse.common.core.annotation.BpLogging;
import cloud.bytepulse.common.core.enums.OperateEnum;
import cloud.bytepulse.common.core.model.ApiResponse;
import cloud.bytepulse.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-26 19:43
 */
@Tag(name = "文件下载")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/authentication/{fileId}")
    @Operation(summary = "文件需要鉴权", hidden = true)
    @BpLogging(value = OperateEnum.QUERY, desc = "文件访问, 非公开")
}
