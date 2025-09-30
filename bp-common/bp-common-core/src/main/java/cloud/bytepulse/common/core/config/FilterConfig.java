package cloud.bytepulse.common.core.config;

import cloud.bytepulse.common.core.filter.ContentCachingWrapperFilter;
import cloud.bytepulse.common.core.filter.SimpleCorsFilter;
import cloud.bytepulse.common.core.filter.SimpleEncodingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Comparator;
import java.util.List;

/**
 * 普通过滤器配置
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 13:12
 */
@Slf4j
@Configuration
public class FilterConfig {

    /**
     * 注册 cors 过滤器
     */
    @Bean
    public FilterRegistrationBean<SimpleCorsFilter> simpleCorsFilterRegistration() {
        FilterRegistrationBean<SimpleCorsFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SimpleCorsFilter());
        registration.addUrlPatterns("/*");
        registration.setName("simpleCorsFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        return registration;
    }

    /**
     * 注册 编码 过滤器
     */
    @Bean
    public FilterRegistrationBean<SimpleEncodingFilter> simpleEncodingFilterRegistration() {
        FilterRegistrationBean<SimpleEncodingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SimpleEncodingFilter());
        registration.addUrlPatterns("/*");
        registration.setName("simpleEncodingFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE); // 最低优先级
        return registration;
    }

    /**
     * 注册 可重复读写包装器 过滤器
     */
    @Bean
    public FilterRegistrationBean<ContentCachingWrapperFilter> contentCachingWrapperFilterRegistration() {
        FilterRegistrationBean<ContentCachingWrapperFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentCachingWrapperFilter());
        registration.addUrlPatterns("/*");
        registration.setName("contentCachingWrapperFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE); // 最高优先级
        return registration;
    }
}
