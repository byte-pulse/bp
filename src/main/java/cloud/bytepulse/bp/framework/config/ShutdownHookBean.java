package cloud.bytepulse.bp.framework.config;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @author jiejiebiezheyang
 * @since 2024-03-01 10:00
 */
@Slf4j
@Component
public class ShutdownHookBean {

    @PreDestroy
    public void destroy() {
        log.info("系统正在停机, 服务即将停止, 再见了世界 (๑•̀ㅂ•́)و");
    }
}
