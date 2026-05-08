package cloud.bytepulse.service.file;

import cloud.bytepulse.data.entity.FileMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 获取文件元信息
     */
    FileMetadata getFileMeta(Long fileId);

    /**
     * 逻辑删除文件
     */
    int deleteById(Long fileId);

    /**
     * 批量物理删除文件
     */
    int deleteByIds(List<Long> ids);

    /**
     * 上传文件到 MinIO 并保存元信息
     */
    void uploadFile(String bizType, String bizId,
                    MultipartFile multipartFile,
                    boolean isPublic, boolean isUnique) throws Exception;
}
