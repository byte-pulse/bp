package cloud.bytepulse.service.logging.impl;

import cloud.bytepulse.data.entity.SysLog;
import cloud.bytepulse.data.mapper.SysLogMapper;
import cloud.bytepulse.service.logging.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志服务
 *
 * @author jiejiebiezheyang
 * @since 2026-09-09 15:11
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LoggingServiceImpl implements LoggingService {

    private final SysLogMapper sysLogMapper;

    /**
     * 保存日志
     *
     * @param sysLog 日志信息
     */
    @Async
    @Override
    public void asyncSaveLog(SysLog sysLog) {
        sysLogMapper.insert(sysLog);
    }
}
