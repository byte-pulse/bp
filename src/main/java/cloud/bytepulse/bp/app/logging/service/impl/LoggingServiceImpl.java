package cloud.bytepulse.bp.app.logging.service.impl;

import cloud.bytepulse.bp.app.logging.service.LoggingService;
import cloud.bytepulse.bp.domain.mapper.SysLogMapper;
import cloud.bytepulse.bp.domain.models.entity.SysLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jiejiebiezheyang
 * @since 2025-12-11 17:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LoggingServiceImpl implements LoggingService {

    private final SysLogMapper sysLogMapper;

    @Async
    public void save(SysLog log) {
        sysLogMapper.insert(log);
    }
}
