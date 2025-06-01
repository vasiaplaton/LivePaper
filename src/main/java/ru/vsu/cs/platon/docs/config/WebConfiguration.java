package ru.vsu.cs.platon.docs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Разрешаем запросы с любых источников (в режиме разработки)
                .allowedOriginPatterns("*")
                // Разрешаем все HTTP-методы (GET, POST, PUT, DELETE и т.п.)
                .allowedMethods("*")
                // Разрешаем клиенту отправлять любые заголовки, включая Authorization
                .allowedHeaders("*")
                // При необходимости можно явно “выставить” заголовки, которые будут видны фронту
                .exposedHeaders("Authorization", "Content-Type")
                // Если нужно передавать куки/учётные данные – включаем эту опцию:
                .allowCredentials(true);
    }
}