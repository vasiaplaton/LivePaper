package ru.vsu.cs.platon.docs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class ClickhouseDatasourceConfig {

    @Value("${spring.secondDatasource.url}")
    private String url;

    @Value("${spring.secondDatasource.username}")
    private String username;

    @Value("${spring.secondDatasource.password}")
    private String password;

    @Bean
    @ConfigurationProperties(prefix="spring.secondDatasource")
    public DataSource clickHouseDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("ru.yandex.clickhouse.ClickHouseDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public JdbcTemplate clickHouseJdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }
}
