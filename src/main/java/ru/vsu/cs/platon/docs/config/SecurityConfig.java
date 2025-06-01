package ru.vsu.cs.platon.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.vsu.cs.platon.docs.config.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // паттерны, которые должны быть публичными
        String[] publicPatterns = new String[]{
                "/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/docs/**", "/openapi.json/**"
        };

        http
                // 1) включаем CORS (Spring Security будет брать конфигурацию из CorsConfigurationSource или WebMvcConfigurer)
                .cors(Customizer.withDefaults())
                // 2) отключаем CSRF, потому что у нас stateless JWT
                .csrf(csrf -> csrf.disable())
                // 3) не используем встроенную сессию
                .sessionManagement(sm -> sm.disable())
                // 4) разрешаем OPTIONS-предварительные запросы без авторизации
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(publicPatterns).permitAll()
                        .anyRequest().authenticated()
                )
                // 5) добавляем фильтр JWT перед UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
