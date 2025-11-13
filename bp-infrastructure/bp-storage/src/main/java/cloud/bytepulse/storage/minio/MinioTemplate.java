package cloud.bytepulse.storage.minio;

import cloud.bytepulse.common.core.exception.BytePulseException;
import cloud.bytepulse.common.core.exception.enums.FileErrorCode;
import cloud.bytepulse.common.core.properties.AppProperties;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 14:45
 */
@Slf4j
@Component
public class MinioTemplate {

    private final MinioClient minioClient;

    private final AppProperties.MinioProperties minioProperties;

    public MinioTemplate(MinioClient minioClient, AppProperties appProperties) {
        this.minioClient = minioClient;
        this.minioProperties = appProperties.getMinio();
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 文件名称
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());
            return true;
        } catch (Exception e) {
            log.info("MinioUtils 文件不存在: {}", objectName);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 文件名称
     */
    public boolean directoryExists(String objectName) {
        objectName = objectName.endsWith("/") ? objectName : objectName + "/";
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .prefix(objectName)  // 以这个前缀开头的文件
                            .recursive(true) // 递归查询
                            .build()
            );

            // 遍历结果，如果有任何文件，就认为目录存在
            for (Result<Item> result : results) {
                if (result.get() != null) {
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            log.info("MinioUtils 目录不存在: {}", objectName);
            return false;
        }
    }

    /**
     * 上传文件
     */
    public void uploadFile(String objectName, File file) throws Exception {
        try (InputStream inputStream = new FileInputStream(file)) {
            Tika tika = new Tika();
            FileInputStream stream = new FileInputStream(file);
            String contentType = tika.detect(stream);
            stream.close();
            Path path = Paths.get(file.getAbsolutePath());
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.length(), -1L)
                    .contentType(contentType)
                    .build());
            log.info("MinioUtils 文件上传成功: {}", objectName);
        } catch (MinioException e) {
            log.info("MinioUtils 文件上传失败: {}", objectName);
            throw new BytePulseException(FileErrorCode.FIle_UPLOAD_FAIL, e);
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 文件对象名
     */
    public void deleteFile(String objectName) throws Exception {
        try {
            if (fileExists(objectName)) {
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(minioProperties.getBucketName()).object(objectName).build());
                log.info("MinioUtils 文件删除成功: {}", objectName);
            }
        } catch (MinioException e) {
            log.info("MinioUtils 文件删除失败: {}", objectName);
            throw new BytePulseException(FileErrorCode.FILE_DELETE_FAIL, e);
        }
    }
}
