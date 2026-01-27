package cloud.bytepulse.bp.app.file.service.impl;

import cloud.bytepulse.bp.app.file.service.FileService;
import cloud.bytepulse.bp.common.util.minio.MinioUtils;
import cloud.bytepulse.bp.domain.entity.FileMetadata;
import cloud.bytepulse.bp.domain.mapper.FileMetadataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-26 19:45
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileMetadataMapper fileMetadataMapper;

    private final MinioUtils minioUtils;

    /**
     * 判断文件是否需要权限, 重定向到指定接口
     *
     * @param fileId 文件id
     *
     */
    @Override
    public ResponseEntity<Void> access(Long fileId) throws NoResourceFoundException {
        FileMetadata fileMetadata = fileMetadataMapper.selectById(fileId);
        if (fileMetadata == null) {
            throw new NoResourceFoundException(HttpMethod.GET, fileId.toString());
        }
        // 判断文件是否需要权限
        boolean needAccess = fileMetadata.getAccessLevel() != 0;
        String redirectUrl = "/file" + (needAccess ? "/private/" : "/public/") + fileId;
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(redirectUrl)).build();
    }

    /**
     * 文件访问, 需要权限
     *
     * @param fileId 文件id
     */
    @Override
    public ResponseEntity<Void> privateAccess(Long fileId) throws Exception {
        FileMetadata fileMetadata = fileMetadataMapper.selectById(fileId);
        if (fileMetadata == null) {
            throw new NoResourceFoundException(HttpMethod.GET, fileId.toString());
        }
        // 获取文件预签名链接
        String preSignedUrl = minioUtils.preSignedUrl(fileMetadata.getObjectName(), 120);
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(preSignedUrl)).build();
    }

    /**
     * 文件访问, 公开
     *
     * @param fileId 文件id
     */
    @Override
    public ResponseEntity<Void> publicAccess(Long fileId) throws Exception {
        FileMetadata fileMetadata = fileMetadataMapper.selectById(fileId);
        if (fileMetadata == null) {
            throw new NoResourceFoundException(HttpMethod.GET, fileId.toString());
        }
        if (fileMetadata.getAccessLevel() != 0) {
            throw new AccessDeniedException("文件不公开");
        }
        // 获取文件预签名链接
        String preSignedUrl = minioUtils.preSignedUrl(fileMetadata.getObjectName(), 120);
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(preSignedUrl)).build();
    }
}
