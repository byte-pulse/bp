package cloud.bytepulse.security.config;

import cloud.bytepulse.cache.redis.RedisCache;
import cloud.bytepulse.common.core.component.LoggingFilterInterface;
import cloud.bytepulse.common.core.constant.ApiPathRegistry;
import cloud.bytepulse.common.core.properties.AppProperties;
import cloud.bytepulse.security.filter.JwtFilter;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 16:32
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoggingFilterInterface loggingFilterInterface;

    /**
     * 用户名密码认证 Provider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailServiceImpl) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailServiceImpl);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    /**
     * 配置密码加密方式
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 默认配置
     */
    @Bean
    @PermitAll
    SecurityFilterChain filterChain(HttpSecurity http, AppProperties appProperties, RedisCache redisCache) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // 关闭 csrf
        http.cors(AbstractHttpConfigurer::disable); // 关闭 cors
        // 设置不通过Session获取SecurityContext
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> {
                    ApiPathRegistry.ANONYMOUS_API.forEach((methodName, urls) -> {
                        // 设置放行接口
                        HttpMethod httpMethod = HttpMethod.valueOf(methodName);
                        for (String url : urls) {
                            auth.requestMatchers(httpMethod, url).permitAll();
                        }
                    });
                    auth.anyRequest().authenticated();
                }
        );

        JwtFilter jwtFilter = new JwtFilter(redisCache, appProperties);
        // 日志过滤器
        http.addFilterBefore(loggingFilterInterface, UsernamePasswordAuthenticationFilter.class);
        // 添加JWT过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider) {
        return new ProviderManager(Collections.singletonList(
                daoAuthenticationProvider//, // 用户名密码认证
                //wechatAuthenticationProvider // 微信认证
        ));
    }
}
