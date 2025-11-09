package cloud.bytepulse.mq.rabbit.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-09 14:48
 */
@Configuration
public class DelayQueue {

    /**
     * 设置延迟队列
     */
    @Bean
    public Queue delay10sQueue() {
        return QueueBuilder.durable("delay10s.queue")
                .withArgument("x-dead-letter-exchange", "dlx.exchange") // 死信交换机
                .withArgument("x-dead-letter-routing-key", "delay10s") // 死信路由
                .withArgument("x-message-ttl", 10_000) // 10秒过期
                .build();
    }
}
