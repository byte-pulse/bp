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
}
