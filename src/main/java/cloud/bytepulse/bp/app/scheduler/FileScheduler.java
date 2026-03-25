package cloud.bytepulse.bp.app.scheduler;

import cloud.bytepulse.bp.common.util.minio.properties.IMinioProperties;
import cloud.bytepulse.bp.domain.entity.FileMetadata;
import cloud.bytepulse.bp.domain.mapper.FileMetadataMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.minio.MinioClient;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.DeleteRequest;
import io.minio.messages.DeleteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

/**
 * 文件相关定时器
 *
 * @author jiejiebiezheyang
 * @since 2026-01-27 14:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileScheduler {

    private final FileMetadataMapper fileMetadataMapper;

    private final MinioClient minioClient;

    private final IMinioProperties iMinioProperties;


    /**
     * 清空 已删除超过 30 天 的文件
     * 每天凌晨2点执行
     */
    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    public void clearDeletedFile() throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        List<FileMetadata> fileMetadataList = fileMetadataMapper.selectList(new LambdaQueryWrapper<FileMetadata>()
                .eq(FileMetadata::getStatus, 0)
                .lt(FileMetadata::getDeleteTime, LocalDate.now().minusDays(30)));

        // minio 对象名
        List<DeleteRequest.Object> objectNames = fileMetadataList.stream().map(f -> new DeleteRequest.Object(f.getObjectName()))
                .toList();

        // minio 删除文件
        Iterable<Result<DeleteResult.Error>> results = minioClient
                .removeObjects(
                        RemoveObjectsArgs.builder()
                                .bucket(iMinioProperties.getBucketName())
                                .objects(objectNames)
                                .build()
                );

        for (Result<DeleteResult.Error> result : results) {
            try {
                DeleteResult.Error error = result.get();
                log.warn("删除失败: {} - {}", error.objectName(), error.message());
            } catch (MinioException e) {
                throw new RuntimeException(e);
            }
        }

        // 删除数据库记录
        List<Long> ids = fileMetadataList.stream().map(FileMetadata::getId).toList();
        fileMetadataMapper.deleteBatchIds(ids);
    }
}
