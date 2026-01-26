package cloud.bytepulse.bp.framework.config;

import cloud.bytepulse.bp.common.util.minio.properties.IMinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return MinioClient.builder().endpoint(
                        iMinioProperties.getEndpoint())
                .credentials(iMinioProperties.getAccessKey(),
                        iMinioProperties.getSecretKey()).build();
    }
}
