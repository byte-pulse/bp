package cloud.bytepulse.bp.framework.lifecycle;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 *
 * @author jiejiebiezheyang
 * @since 2024-03-01 10:00
 */
@Slf4j
@Component
public class ApplicationLifecycleListener {

    @Value("${server.port:8080}") // 获取端口, 默认8080
    private String port;

    private final Environment environment;

    public ApplicationLifecycleListener(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openSwagger() {
        try {
            // 1. 判断操作系统
            String os = System.getProperty("os.name").toLowerCase();
            boolean isWindows = os.contains("windows");

            if (!isWindows) {
                return;
            }

            // 2. 判断 profile
            String[] activeProfiles = environment.getActiveProfiles();
            boolean isDevOrTest = Arrays.stream(activeProfiles)
                    .anyMatch(p -> p.equalsIgnoreCase("dev") || p.equalsIgnoreCase("test"));

            if (!isDevOrTest) {
                return;
            }

            // 3. 拼接 swagger 地址
            String url = "http://127.0.0.1:" + port + "/swagger-ui/index.html";

            log.info("检测到开发环境, 点击打开 Swagger: {}", url);

            // 4. 打开浏览器
            // if (Desktop.isDesktopSupported()) {
            //     Desktop.getDesktop().browse(new URI(url));
            // } else {
            //     // 兼容 fallback
            //     ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", url);
            //     processBuilder.start();
            // }

        } catch (Exception e) {
            log.error("打开浏览器失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("系统正在停机, 服务即将停止, 再见了世界 (๑•̀ㅂ•́)و");
    }
}
