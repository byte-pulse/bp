package cloud.bytepulse.service.file.impl;

import cloud.bytepulse.common.core.exception.BytePulseException;
import cloud.bytepulse.common.core.exception.enums.FileErrorCode;
import cloud.bytepulse.common.core.properties.AppProperties;
import cloud.bytepulse.data.entity.FileMetadata;
import cloud.bytepulse.data.mapper.FileMetadataMapper;
import cloud.bytepulse.service.file.FileService;
import cloud.bytepulse.storage.minio.MinioTemplate;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

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

    private final MinioTemplate minioTemplate;

    private final MinioClient minioClient;

    private final AppProperties appProperties;

    /**
     * 生成 MinIO 文件路径
     *
     * @param bizType 业务类型. 如 order. avatar. contract
     * @param bizId   业务 ID. 如订单 ID. 用户 ID
     * @param fileId  文件 ID. 如图片 ID. 合同 ID
     * @param ext     文件扩展名. 不带点. 如 pdf jpg
     * @return MinIO object path
     */
    private static String buildPath(
            String bizType,
            String bizId,
            Long fileId,
            String ext
    ) {
        LocalDate now = LocalDate.now();

        String datePath = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String bizSegment = (bizId == null) ? "_tmp" : bizId;
        String suffix = (ext == null || ext.isBlank()) ? "" : "." + ext.toLowerCase();

        return String.format(
                "%s/%s/%s/%d%s",
                bizType.toLowerCase(),
                datePath,
                bizSegment,
                fileId,
                suffix
        );
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
            throw new NoResourceFoundException(HttpMethod.GET, fileId.toString(), fileId.toString());
        }
        // 获取文件预签名链接
        String preSignedUrl = minioTemplate.preSignedUrl(fileMetadata.getObjectName(), 120);
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(preSignedUrl)).build();
    }

    /**
     * 文件访问
     *
     * @param fileId 文件id
     */
    @Override
    public ResponseEntity<Void> access(Long fileId) throws Exception {
        FileMetadata fileMetadata = fileMetadataMapper.selectById(fileId);
        if (fileMetadata == null) {
            throw new NoResourceFoundException(HttpMethod.GET, fileId.toString(), fileId.toString());
        }
        if (fileMetadata.getAccessLevel() != 0) {
            // 不是公开文件, 重定向 到 可以鉴权的接口
            String redirectUrl = "/file/authentication/" + fileId;
            return ResponseEntity.status(HttpStatus.FOUND) // 302
                    .location(URI.create(redirectUrl)).build();
        }
        // 获取文件预签名链接
        String preSignedUrl = minioTemplate.preSignedUrl(fileMetadata.getObjectName(), 120);
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(preSignedUrl)).build();
    }

    /**
     * 获取文件元信息
     *
     * @param fileId 文件id
     */
    @Override
    public FileMetadata getFileMeta(Long fileId) {
        return fileMetadataMapper.selectById(fileId);
    }

    /**
     * 删除文件
     *
     * @param fileId 文件id
     */
    @Override
    public int deleteById(Long fileId) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(fileId);
        fileMetadata.setStatus(0); // 逻辑删除
        fileMetadata.setDeleteTime(new Date());
        return fileMetadataMapper.updateById(fileMetadata);
    }

    /**
     * 删除文件 批量
     *
     * @param ids 文件id
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        return fileMetadataMapper.deleteByIds(ids);
    }

    /**
     * 上传文件到 minio 并保存信息
     *
     * @param bizType       业务类型
     * @param bizId         业务id
     * @param multipartFile 文件
     * @param isPublic      是否公开
     * @param isUnique      是否唯一
     */
    @Override
    public void uploadFile(String bizType, String bizId,
                           MultipartFile multipartFile,
                           boolean isPublic, boolean isUnique)
            throws Exception {
        // 如果是唯一
        if (isUnique) {
            // 判断此业务是否已存在关联文件
            FileMetadata fileMetadata = fileMetadataMapper.selectOne(new LambdaQueryWrapper<FileMetadata>()
                    .eq(FileMetadata::getStatus, 1)
                    .eq(FileMetadata::getBizType, bizType)
                    .eq(FileMetadata::getBizId, bizId));
            if (fileMetadata != null) {
                // 先逻辑删除
                fileMetadata.setStatus(0);
                fileMetadata.setDeleteTime(new Date());
                fileMetadataMapper.updateById(fileMetadata);
            }
        }
        // 生成文件id
        Long fileId = IdWorker.getId();
        // 原文件名
        String originalFilename = multipartFile.getOriginalFilename();
        // minio 对象名
        int dotIndex = 0;
        if (originalFilename != null) {
            dotIndex = originalFilename.lastIndexOf('.');
        }
        String ext = "";
        if (originalFilename != null && dotIndex != -1 && dotIndex < originalFilename.length() - 1) {
            ext = originalFilename.substring(dotIndex + 1);
        }
        String objectName = buildPath(bizType, bizId, fileId, ext);
        // 获取 content_type
        String contentType = multipartFile.getContentType();
        // 文件大小
        long size = multipartFile.getSize();
        // 访问级别
        Integer accessLevel = isPublic ? 0 : 1;


        // 保存文件元信息
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setId(fileId);
        fileMetadata.setFileName(originalFilename);
        fileMetadata.setObjectName(objectName);
        fileMetadata.setContentType(contentType);
        fileMetadata.setSize(size);
        fileMetadata.setAccessLevel(accessLevel);
        fileMetadata.setBizType(bizType);
        fileMetadata.setBizId(bizId);
        fileMetadata.setStatus(1);
        fileMetadata.setCreateTime(new Date());
        fileMetadataMapper.insert(fileMetadata);

        // 上传到 minio
        try (InputStream is = multipartFile.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(appProperties.getMinio().getBucketName())
                            .object(objectName)
                            .stream(is, size, -1L) // 第三个参数是 partSize, -1 表示 SDK 自动处理
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new BytePulseException(FileErrorCode.FIle_UPLOAD_FAIL);
        }
    }
}
