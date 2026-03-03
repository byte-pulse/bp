package cloud.bytepulse.bp.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口文档配置类
 *
 * @author jiejiebiezheyang
 * @since 2024-03-01 10:00
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
    private String version = "1.0.0";


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

    private Components components() {
        return new Components()
                .addSecuritySchemes("Authorization",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("Authorization"));
    }

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

    // 在配置文件中定义分组
    @Bean
    public List<ApiGroup> apiGroups() {
        Map<String, List<String>> paths = new HashMap<>();
        for (ApiGroup apiGroup : API_GROUPS) {
            // 根据 key 分组, 没有先创建
            paths.computeIfAbsent(apiGroup.getName(), k -> new ArrayList<>());
            paths.get(apiGroup.getName()).addAll(apiGroup.getPaths());
        }
        // paths 转为 ApiGroup 集合
        List<ApiGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : paths.entrySet()) {
            groups.add(new ApiGroup(entry.getKey(), entry.getValue()));
        }
        groups.add(new ApiGroup("A全部接口A", "/**"));
        return groups;
    }

    // 批量注册分组
    @Bean
    public List<GroupedOpenApi> groupedOpenApis(List<ApiGroup> groups) {
        return groups.stream()
                .map(group -> GroupedOpenApi.builder()
                        .group(group.getName())
                        .pathsToMatch(group.getPaths().toArray(new String[0]))
                        .build())
                .collect(Collectors.toList());
    }

    // 简单的分组配置类
    @Data
    @AllArgsConstructor
    public static class ApiGroup {
        private String name;
        private List<String> paths;

        public ApiGroup(String name, String... paths) {
            this.name = name;
            this.paths = Arrays.asList(paths);
        }
    }
}