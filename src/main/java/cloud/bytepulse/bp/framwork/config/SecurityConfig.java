package cloud.bytepulse.bp.framwork.config;

import cloud.bytepulse.bp.framwork.constant.AnonymousConstant;
import cloud.bytepulse.bp.framwork.filtter.GlobalCorsFilter;
import cloud.bytepulse.bp.framwork.filtter.JWTFilter;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    private final GlobalCorsFilter globalCorsFilter;

    private final JWTFilter jwtFilter;

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
        String[] anonymous = AnonymousConstant.ANONYMOUS.toArray(new String[]{});
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(httpRequest -> httpRequest
                .requestMatchers(anonymous).permitAll() // 允许actuator
                .anyRequest().authenticated());

        // 全局CORS过滤器
        http.addFilterBefore(globalCorsFilter, UsernamePasswordAuthenticationFilter.class);
        // 添加JWT过滤器
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
