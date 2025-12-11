package cloud.bytepulse.bp.app.logging.service.impl;

import cloud.bytepulse.bp.app.logging.service.LoggingService;
import cloud.bytepulse.bp.domain.mapper.SysLogMapper;
import cloud.bytepulse.bp.domain.models.entity.SysLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author jiejiebiezheyang
 * @since 2025-12-11 17:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoggingServiceImpl implements LoggingService {

    private final SysLogMapper sysLogMapper;

    @Async("dbExecutor")
    public void save(SysLog log) {
        sysLogMapper.insert(log);
    }
}
