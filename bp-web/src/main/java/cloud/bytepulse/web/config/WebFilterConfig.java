package cloud.bytepulse.web.config;

import cloud.bytepulse.service.logging.LoggingService;
import cloud.bytepulse.web.logging.LoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-09 15:16
 */
@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {

    /**
     * 注册日志过滤器
     */
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter(LoggingService loggingService) {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter(loggingService)); // 构造函数注入
        registrationBean.addUrlPatterns("/*"); // 拦截路径
        registrationBean.setOrder(1); // 执行顺序
        return registrationBean;
    }
}
