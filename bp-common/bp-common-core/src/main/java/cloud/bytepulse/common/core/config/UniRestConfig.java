package cloud.bytepulse.common.core.config;

import jakarta.annotation.PostConstruct;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author jiejiebiezheyang
 * @since 2025-04-02 20:00
 */
@Configuration
@RequiredArgsConstructor
public class UniRestConfig {

    private final JsonMapper jsonMapper;

    @PostConstruct
    public void config() {
        Unirest.config().setObjectMapper(new kong.unirest.core.ObjectMapper() {
            @Override
            public <T> T readValue(String value, Class<T> aClass) {
                try {
                    return jsonMapper.readValue(value, aClass);
                } catch (JacksonException e) {
                    throw new UnirestException("Failed to parse JSON: " + value, e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return jsonMapper.writeValueAsString(value);
                } catch (JacksonException e) {
                    throw new UnirestException("Failed to serialize object to JSON", e);
                }
            }
        });

        Unirest.config()
                .connectTimeout(10_000)    // 10 秒
                .requestTimeout(30_000)     // 30 秒
                .retryAfter(true, 3)    // 自动重试次数
                .enableCookieManagement(false); // 关闭cookie
    }
}
