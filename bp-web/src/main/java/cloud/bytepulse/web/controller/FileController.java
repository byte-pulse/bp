package cloud.bytepulse.web.controller;

import cloud.bytepulse.common.core.annotation.Anonymous;
import cloud.bytepulse.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> privateAccess(@PathVariable Long fileId) throws Exception {
        return fileService.privateAccess(fileId);
    }

    @GetMapping("/access/{fileId}")
    @Operation(summary = "文件访问, 公开")
    @Anonymous
    public ResponseEntity<Void> access(@PathVariable Long fileId) throws Exception {
        return fileService.access(fileId);
    }
}
