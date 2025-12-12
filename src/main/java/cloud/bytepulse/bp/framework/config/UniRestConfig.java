package cloud.bytepulse.bp.framework.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiejiebiezheyang
 * @since 2025-04-02 20:00
 */
@Configuration
@RequiredArgsConstructor
public class UniRestConfig {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void config() {
        Unirest.config().setObjectMapper(new kong.unirest.core.ObjectMapper() {
            @Override
            public <T> T readValue(String value, Class<T> aClass) {
                try {
                    return objectMapper.readValue(value, aClass);
                } catch (JsonProcessingException e) {
                    throw new UnirestException("Failed to parse JSON: " + value, e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return objectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
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
