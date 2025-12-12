package cloud.bytepulse.bp.common.utils.minio;

import cloud.bytepulse.bp.common.utils.minio.properties.IMinioProperties;
import cloud.bytepulse.bp.framework.exception.BytePulseException;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
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
 * @since 2025-03-16 18:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtils {

    private final MinioClient minioClient;

    private final IMinioProperties iMinioProperties;

    /**
     * 检查文件是否存在
     *
     * @param objectName 文件名称
     */
    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(iMinioProperties.getBucketName())
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
                            .bucket(iMinioProperties.getBucketName())
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
                    .bucket(iMinioProperties.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.length(), -1)
                    .contentType(contentType)
                    .build());
            log.info("MinioUtils 文件上传成功: {}", objectName);
        } catch (MinioException e) {
            log.info("MinioUtils 文件上传失败: {}", objectName);
            throw new BytePulseException(e.getMessage());
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
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(iMinioProperties.getBucketName()).object(objectName).build());
                log.info("MinioUtils 文件删除成功: {}", objectName);
            }
        } catch (MinioException e) {
            log.info("MinioUtils 文件删除失败: {}", objectName);
            throw new BytePulseException(e.getMessage());
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
                        .bucket(iMinioProperties.getBucketName())
                        .prefix(directoryPath)
                        .recursive(true)
                        .build());

        // 先删除目录下的所有对象
        for (Result<Item> result : results) {
            Item item = result.get();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(iMinioProperties.getBucketName())
                            .object(item.objectName())
                            .build());
        }

        // 然后删除目录本身（在MinIO中目录是虚拟的，但可以删除空"目录"）
        try {
            // 尝试删除目录标记（如果有）
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(iMinioProperties.getBucketName())
                            .object(directoryPath)
                            .build());
            log.info("MinioUtils 目录删除成功: {}", directoryPath);
            return true;
        } catch (Exception e) {
            log.info("MinioUtils 目录删除失败: {}", directoryPath);
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取文件信息
     *
     * @param objectName 文件名称
     */
    public StatObjectResponse fileInfo(String objectName) throws Exception {
        if (!fileExists(objectName)) {
            throw new BytePulseException("文件不存在");
        }
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(iMinioProperties.getBucketName())
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
                            .bucket(iMinioProperties.getBucketName())
                            .prefix(directoryPath)
                            .recursive(recursive) // 递归子目录
                            .build()
            );

            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            throw new BytePulseException(e.getMessage());
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
            throw new BytePulseException(e.getMessage());
        }
        return fileNames;
    }

    /**
     * 生成临时访问 URL（预签名 URL）
     *
     * @param objectName 文件对象名
     * @param expiry     链接有效时长(分钟)
     */
    public String preSignedUrl(String objectName, int expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(iMinioProperties.getBucketName())
                        .object(objectName)
                        .method(Method.GET)  // 生成 GET 方式的 URL
                        .expiry(expiry, TimeUnit.MINUTES) // 设置 URL 过期时间
                        .build()
        );
    }

    /**
     * 生成临时访问 URL（预签名 URL）
     *
     * @param objectName 文件对象名
     */
    public String preSignedUrl(String objectName) throws Exception {
        StatObjectResponse fileInfo = fileInfo(objectName);
        long size = fileInfo.size();
        int expiry;
        if (size < 10 * 1024 * 1024) {
            // <10MB，10分钟
            expiry = 10;
        } else if (size < 100 * 1024 * 1024) {
            // 10MB-100MB，60分钟
            expiry = 60;
        } else {
            // >100MB，24小时
            expiry = 60 * 24;
        }
        return preSignedUrl(objectName, expiry);
    }

    /**
     * 下载文件
     *
     * @param objectName 文件对象名
     */
    public InputStream downloadFile(String objectName) throws Exception {
        try {
            if (!fileExists(objectName)) {
                throw new BytePulseException("文件不存在");
            }
            return minioClient.getObject(GetObjectArgs.builder().bucket(iMinioProperties.getBucketName()).object(objectName).build());
        } catch (MinioException e) {
            throw new BytePulseException(e.getMessage());
        }
    }


    /**
     * 返回给前端下载
     *
     * @param objectName 文件对象名
     * @param filename   指定文件名
     */
    public ResponseEntity<InputStreamResource> returnFile(String objectName, String filename, boolean download) throws Exception {
        if (!fileExists(objectName)) {
            throw new BytePulseException("文件不存在");
        }
        InputStream inputStream = downloadFile(objectName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // 标记流的起始位置
        bufferedInputStream.mark(Integer.MAX_VALUE);

        //  检测文件类型
        StatObjectResponse statObjectResponse = fileInfo(objectName);
        String contentType = statObjectResponse.contentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        // 重置流到起始位置
        bufferedInputStream.reset();

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        if (download) {
            // 强制下载：同时提供旧版 filename 和 UTF-8 编码的 filename*
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encodedFileName);
        } else {
            // 内联显示：优先用 filename*=UTF-8''，但部分浏览器可能不支持，所以也提供 filename
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + filename + "\"; filename*=UTF-8''" + encodedFileName);
        }
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        return ResponseEntity.ok().headers(headers).body(new InputStreamResource(bufferedInputStream));
    }

    /**
     * 返回给前端下载
     * 自动获取文件名
     *
     * @param objectName 文件对象名|文件名
     */
    public ResponseEntity<InputStreamResource> returnFile(String objectName, boolean download) throws Exception {
        if (!fileExists(objectName)) {
            throw new BytePulseException("文件不存在");
        }
        StatObjectResponse fileInfo = fileInfo(objectName);
        String filename = extractFileName(fileInfo.object());
        return returnFile(objectName, filename, download);
    }
}
