package cloud.bytepulse.bp.framework.config;

import cloud.bytepulse.bp.common.util.minio.properties.IMinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author jiejiebiezheyang
 * @since 2025-03-16 17:00
 */
@Configuration()
@RequiredArgsConstructor
public class MinioConfig {

    private final IMinioProperties iMinioProperties;

    @Bean
    public MinioClient minioClient() {
        // 自定义 OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)) // 最大20连接，空闲存活5分钟
                .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
                .readTimeout(60, TimeUnit.SECONDS)    // 读超时
                .writeTimeout(60, TimeUnit.SECONDS)   // 写超时
                .build();

        return MinioClient.builder().endpoint(
                        iMinioProperties.getEndpoint())
                .httpClient(okHttpClient)
                .credentials(iMinioProperties.getAccessKey(),
                        iMinioProperties.getSecretKey()).build();
    }
}
