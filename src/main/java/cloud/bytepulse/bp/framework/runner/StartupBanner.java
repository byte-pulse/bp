package cloud.bytepulse.bp.framework.runner;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 启动后打印 banner
 *
 * @author jiejiebiezheyang
 * @since 2024-06-26 00:00
 */
@Component
public class StartupBanner {

    @Bean
    public ApplicationRunner bannerRunner() {
        return args -> {
            ClassPathResource resource = new ClassPathResource("startup.txt");

            // 判断文件是否存在
            if (!resource.exists()) {
                return;
            }

            try (var inputStream = resource.getInputStream()) {
                String banner = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("\u001B[32m" + banner + "\u001B[0m");
            } catch (Exception ignored) {
            }
        };
    }
}