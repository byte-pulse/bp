package cloud.bytepulse.bp.framework.config;

import cloud.bytepulse.bp.app.auth.authentication.provider.WechatAuthenticationProvider;
import cloud.bytepulse.bp.common.constant.ControllerApiConstant;
import cloud.bytepulse.bp.framework.filter.ExternalApiFilter;
import cloud.bytepulse.bp.framework.filter.GlobalCorsFilter;
import cloud.bytepulse.bp.framework.filter.JWTFilter;
import cloud.bytepulse.bp.framework.filter.LoggingFilter;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Arrays;

/**
 * Security配置类
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 18:06
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoggingFilter loggingFilter;

    private final GlobalCorsFilter globalCorsFilter;

    private final JWTFilter jwtFilter;

    private final ExternalApiFilter externalApiFilter;

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
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // 关闭 csrf
        http.cors(AbstractHttpConfigurer::disable); // 关闭 cors
        // 设置不通过Session获取SecurityContext
        String[] anonymous = ControllerApiConstant.ANONYMOUS_API.toArray(new String[]{});
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(httpRequest -> httpRequest
                .requestMatchers(anonymous).permitAll() // 允许匿名访问
                .anyRequest().authenticated());

        // 日志过滤器
        http.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);
        // 全局CORS过滤器
        http.addFilterBefore(globalCorsFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加JWT过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // 外部接口过滤器
        http.addFilterBefore(externalApiFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider,
            WechatAuthenticationProvider wechatAuthenticationProvider) {
        return new ProviderManager(Arrays.asList(
                daoAuthenticationProvider, // 用户名密码认证
                wechatAuthenticationProvider // 微信认证
        ));
    }
}
