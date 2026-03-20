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


    /**
     * 输出当前过滤器链
     */
    @Bean
    public CommandLineRunner printFilterOrder(List<FilterRegistrationBean<?>> filters) {
        return args -> {
            List<FilterRegistrationBean<?>> sortedFilters = filters.stream()
                    .filter(f -> f.getFilter() != null)
                    .sorted(Comparator.comparingInt(FilterRegistrationBean::getOrder))
                    .toList();

            int orderWidth = Math.max(
                    "Order".length(),
                    sortedFilters.stream()
                            .map(FilterRegistrationBean::getOrder)
                            .map(String::valueOf)
                            .mapToInt(String::length)
                            .max()
                            .orElse(0)
            );

            int nameWidth = Math.max(
                    "Name".length(),
                    sortedFilters.stream()
                            .map(FilterRegistrationBean::getFilterName)
                            .mapToInt(name -> name == null ? 0 : name.length())
                            .max()
                            .orElse(0)
            );

            int classWidth = Math.max(
                    "Class".length(),
                    sortedFilters.stream()
                            .map(f -> f.getFilter().getClass().getName())
                            .mapToInt(String::length)
                            .max()
                            .orElse(0)
            );

            String format = "| %-" + orderWidth + "s | %-" + nameWidth + "s | %-" + classWidth + "s |%n";

            String separator =
                    "+" + "-".repeat(orderWidth + 2)
                            + "+" + "-".repeat(nameWidth + 2)
                            + "+" + "-".repeat(classWidth + 2)
                            + "+";

            StringBuilder sb = new StringBuilder();

            sb.append(separator).append('\n');
            sb.append(String.format(format, "Order", "Name", "Class"));
            sb.append(separator).append('\n');

            for (FilterRegistrationBean<?> filter : sortedFilters) {
                String filterName = filter.getFilterName() == null
                        ? ""
                        : filter.getFilterName();

                sb.append(String.format(
                        format,
                        filter.getOrder(),
                        filterName,
                        filter.getFilter().getClass().getName()
                ));
            }

            sb.append(separator);

            log.debug("Servlet Filter Chain:\n{}", sb);
        };
    }
}
