package cloud.bytepulse.bp.app.file.service;

import org.springframework.http.ResponseEntity;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-26 19:44
 */
public interface FileService {

    /**
     * 文件访问, 需要权限
     */
    ResponseEntity<Void> privateAccess(Long fileId) throws Exception;

    /**
     * 文件访问
     */
    ResponseEntity<Void> access(Long fileId) throws Exception;
}
