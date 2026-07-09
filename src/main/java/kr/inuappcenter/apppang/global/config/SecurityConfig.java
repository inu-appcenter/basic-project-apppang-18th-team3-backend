package kr.inuappcenter.apppang.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())              // 테스트 편의상 CSRF 끔
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()              // TODO: 나중에 JWT 인증으로 교체
                );
        return http.build();
    }
}