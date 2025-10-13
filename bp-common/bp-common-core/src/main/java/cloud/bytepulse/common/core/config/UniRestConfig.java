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
}
