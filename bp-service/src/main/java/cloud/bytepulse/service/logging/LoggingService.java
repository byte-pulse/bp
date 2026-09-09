package cloud.bytepulse.service.logging;

import cloud.bytepulse.data.entity.SysLog;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-09 15:11
 */
public interface LoggingService {
    /**
     * 保存日志
     */
    void asyncSaveLog(SysLog sysLog);
}
