package cloud.bytepulse.common.core.listener;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 16:52
 */
@Slf4j
@Component
public class ApplicationLifecycleListener {

    @PreDestroy
    public void destroy() {
        log.info("系统正在停机, 服务即将停止, 再见了世界 (๑•̀ㅂ•́)و");
    }
}
