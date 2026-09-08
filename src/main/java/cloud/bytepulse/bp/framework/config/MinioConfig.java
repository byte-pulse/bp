package cloud.bytepulse.bp.framework.config;

import cloud.bytepulse.bp.common.properties.AppProperties;
import io.minio.MinioClient;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author jiejiebiezheyang
 * @since 2025-03-16 17:00
 */
@Configuration
public class MinioConfig {

    private final AppProperties.Minio minio;

    public MinioConfig(AppProperties appProperties) {
        this.minio = appProperties.getMinio();
    }

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
                        minio.getEndpoint())
                .httpClient(okHttpClient)
                .credentials(minio.getAccessKey(),
                        minio.getSecretKey()).build();
    }
}
