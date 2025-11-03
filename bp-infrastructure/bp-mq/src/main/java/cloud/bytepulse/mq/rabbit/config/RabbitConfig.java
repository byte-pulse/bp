package cloud.bytepulse.mq.rabbit.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-09 14:49
 */
@Configuration
public class RabbitConfig {

    /**
     * 设置消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
