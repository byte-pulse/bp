package cloud.bytepulse.bp.common.util.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jiejiebiezheyang
 * @since 2025-03-16 18:00
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class IMinioProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
