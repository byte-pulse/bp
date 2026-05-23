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

    /**
     * 删除指定目录及其所有内容
     *
     * @param directoryPath 要删除的目录路径（以/结尾）
     */
    public boolean deleteDirectory(String directoryPath) throws Exception {
        // 确保目录路径以/结尾
        if (!directoryPath.endsWith("/")) {
            directoryPath += "/";
        }

        // 列出目录下所有对象
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .prefix(directoryPath)
                        .recursive(true)
                        .build());

        // 先删除目录下的所有对象
        for (Result<Item> result : results) {
            Item item = result.get();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(item.objectName())
                            .build());
        }

        // 然后删除目录本身（在MinIO中目录是虚拟的，但可以删除空"目录"）
        try {
            // 尝试删除目录标记（如果有）
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(directoryPath)
                            .build());
            log.info("MinioUtils 目录删除成功: {}", directoryPath);
            return true;
        } catch (Exception e) {
            log.info("MinioUtils 目录删除失败: {}", directoryPath);
            throw new BytePulseException(FileErrorCode.DIR_DELETE_FAIL, e);
        }
    }

    /**
     * 获取文件信息
     *
     * @param objectName 文件名称
     */
    public StatObjectResponse fileInfo(String objectName) throws Exception {
        if (!fileExists(objectName)) {
            throw new BytePulseException(FileErrorCode.FILE_NOT_EXIST);
        }
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .build());
    }

    /**
     * 从对象路径中提取纯文件名
     *
     * @param objectPath 对象路径
     */
    private String extractFileName(String objectPath) {
        if (objectPath == null || objectPath.isEmpty()) {
            return objectPath;
        }

        // 处理路径分隔符（兼容Windows和Unix风格）
        String normalizedPath = objectPath.replace('\\', '/');
        int lastSeparator = normalizedPath.lastIndexOf('/');

        return lastSeparator >= 0
                ? normalizedPath.substring(lastSeparator + 1)
                : normalizedPath;
    }

    /**
     * 列出存储桶中的所有文件
     *
     * @param directoryPath 目录路径 (例如: "my-folder/")
     * @param recursive     是否递归子目录
     */
    public List<Item> fileList(String directoryPath, boolean recursive) {
        // 确保目录路径以/结尾
        if (!directoryPath.endsWith("/")) {
            directoryPath += "/";
        }
        List<Item> items = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .prefix(directoryPath)
                            .recursive(recursive) // 递归子目录
                            .build()
            );

            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            throw new BytePulseException(FileErrorCode.DIR_LIST_FAIL, e);
        }
        return items;
    }

    /**
     * 列出指定目录下的所有文件名
     *
     * @param directoryPath 目录路径 (例如: "my-folder/")
     * @param recursive     是否递归子目录
     */
    public List<String> fileNameList(String directoryPath, boolean recursive) {
        List<String> fileNames = new ArrayList<>();
        try {
            List<Item> items = fileList(directoryPath, recursive);

            for (Item item : items) {
                String objectName = item.objectName().replace(directoryPath, "");
                objectName = objectName.startsWith("/") ? objectName.substring(1) : objectName;
                fileNames.add(objectName);
            }
        } catch (Exception e) {
            throw new BytePulseException(FileErrorCode.DIR_LIST_FAIL, e);
        }
        return fileNames;
    }
}
