package cloud.bytepulse.bp.app.logging.service;

import cloud.bytepulse.bp.domain.models.entity.SysLog;
import org.springframework.scheduling.annotation.Async;

/**
 * @author jiejiebiezheyang
 * @since 2025-12-11 17:12
 */
public interface LoggingService {

    @Async
    void save(SysLog log);
}
