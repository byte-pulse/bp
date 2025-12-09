package cloud.bytepulse.bp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-01 10:00
 */
@Slf4j
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@SpringBootApplication
@MapperScan("cloud.bytepulse.**.mapper")
public class AdminApplication {
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        System.setProperty("pagehelper.banner", "false");
        System.setProperty("log4j.skipJansi", "false");
        SpringApplication.run(AdminApplication.class, args);
        long endTime = System.currentTimeMillis();
        log.info("\u001B[36m应用启动成功, 耗时: {} \u001B[0m", endTime - startTime);
    }
}
