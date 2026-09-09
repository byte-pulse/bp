package cloud.bytepulse;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-07 17:09
 */
@Slf4j
@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class BootApplication {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.setProperty("log4j.skipJansi", "false");
        SpringApplication.run(BootApplication.class, args);
        long endTime = System.currentTimeMillis();
        log.info("\u001B[36m应用启动成功, 耗时: {} \u001B[0m", endTime - startTime);
    }
}
