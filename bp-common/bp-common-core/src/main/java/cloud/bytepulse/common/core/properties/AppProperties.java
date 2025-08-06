package cloud.bytepulse.common.core.properties;

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
@ConfigurationProperties(prefix = "bp.app")
public class AppProperties {

    @Valid
    @NotNull
    private Login login;

    @Valid
    @NotNull
    private RedisProperties redis;

    @Valid
    @NotNull
    private ExternalApiProperties externalApi;

    @Valid
    @NotNull
    private MinioProperties minio;

    @Valid
    @NotNull
    private CaptchaProperties captcha;

    @Data
    public static class Login {
        /**
         * 登陆过期时间
         */
        @NotNull
        private Long expirationMinutes = 30L;
    }

    @Data
    public static class RedisProperties {
        /**
         * redis 键 前缀
         */
        @NotBlank
        private String prefix = "bp:";
    }
}
