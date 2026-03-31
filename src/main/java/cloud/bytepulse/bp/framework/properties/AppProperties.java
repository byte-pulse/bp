package cloud.bytepulse.bp.framework.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author jiejiebiezheyang
 * @since 2026-03-26 14:48
 */
@Data
@Validated
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @Valid
    @NotNull
    private Login login;

    @Valid
    @NotNull
    private Redis redis;

    @Valid
    @NotNull
    private ExternalApi externalApi;

    @Valid
    @NotNull
    private Minio minio;

    @Valid
    @NotNull
    private Captcha captcha;

    @Data
    public static class Login {
        /**
         * 登陆过期时间
         */
        @NotNull
        private Long expirationMinutes = 30L;
    }

    @Data
    public static class Redis {
        /**
         * redis 键 前缀
         */
        @NotBlank
        private String prefix = "bp:";
    }

    @Data
    public static class ExternalApi {
        /**
         * 解密用户密钥
         */
        @NotBlank
        private String secretKey;
        /**
         * iv
         */
        @NotBlank
        private String iv;
    }

    @Data
    public static class Minio {

        /**
         * minio 服务器地址
         */
        @NotBlank
        private String endpoint;
        /**
         * minio 访问 key
         */
        @NotBlank
        private String accessKey;
        /**
         * minio 访问密钥
         */
        @NotBlank
        private String secretKey;
        /**
         * minio 存储桶名称
         */
        @NotBlank
        private String bucketName;
    }

    /**
     * 验证码配置
     */
    @Data
    public static class Captcha {
        @NotNull
        private Integer width = 160;
        @NotNull
        private Integer height = 60;
        @NotNull
        private Integer length = 4;
        @NotNull
        private Long expirationSeconds = 30L;
    }
}
