package cloud.bytepulse.data.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 15:06
 */
@Configuration
@MapperScan("cloud.bytepulse.**.mapper")
public class MyBatisConfig {
}
