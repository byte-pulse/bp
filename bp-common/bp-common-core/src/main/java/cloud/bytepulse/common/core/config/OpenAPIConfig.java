package cloud.bytepulse.common.core.config;

import cloud.bytepulse.common.core.annotation.Pageable;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 12:02
 */
@Configuration
@ConditionalOnProperty(
        name = "springdoc.api-docs.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class OpenAPIConfig {

    public static List<ApiGroup> API_GROUPS = new ArrayList<>();
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${swagger.info.project.url}")
    private String projectUrl;
    @Value("${swagger.info.project.name}")
    private String name;
    @Value("${swagger.info.project.desc}")
    private String desc;
    @Value("${swagger.info.project.email}")
    private String email;
    @Value("${swagger.info.project.version}")
    private String version;


    public OpenAPIConfig(SwaggerUiConfigProperties config) {
        // 设置文档折叠方式 （可选值：none, list, full）
        config.setDocExpansion("none");
        // 设置标签排序方式
        config.setTagsSorter("alpha");
        // 设置操作排序方式
        config.setOperationsSorter("alpha");
        // 设置模型展开深度
        config.setDefaultModelsExpandDepth(-1);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(
                        new Info()
                                .title(name + " API 文档")
                                .description(desc)
                                .contact(new Contact()
                                        .name(name)
                                        .email(email)
                                        .url(projectUrl))
                                .version(version)
                ).components(components())
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }
}
