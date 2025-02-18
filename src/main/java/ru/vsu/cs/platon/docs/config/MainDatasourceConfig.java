package ru.vsu.cs.platon.docs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class MainDatasourceConfig {
    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource primaryDataSource() {
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalStateException("spring.datasource.url is missing in application.properties");
        }

        return DataSourceBuilder.create()
                .url(databaseUrl)
                .username(databaseUsername)
                .password(databasePassword)
                .driverClassName(driverClassName)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }

}
