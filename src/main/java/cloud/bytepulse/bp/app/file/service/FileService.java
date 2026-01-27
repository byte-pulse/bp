package cloud.bytepulse.bp.app.file.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-26 19:44
 */
public interface FileService {

    /**
     * 判断文件是否需要权限, 重定向到指定接口
     */
    ResponseEntity<Void> access(Long fileId) throws NoResourceFoundException;

    /**
     * 文件访问, 需要权限
     */
    ResponseEntity<Void> privateAccess(Long fileId) throws Exception;

    /**
     * 文件访问, 公开
     */
    ResponseEntity<Void> publicAccess(Long fileId) throws Exception;
}
