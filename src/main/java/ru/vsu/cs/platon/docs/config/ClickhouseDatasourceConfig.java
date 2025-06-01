package ru.vsu.cs.platon.docs.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


public class ClickhouseDatasourceConfig {

    // Эти значения будут браться из переменных окружения
    @Value("${CLICKHOUSE_URL}")
    private String url;

    @Value("${CLICKHOUSE_USERNAME}")
    private String username;

    @Value("${CLICKHOUSE_PASSWORD:}")  // пустая строка по умолчанию
    private String password;

    /** Основной DataSource для ClickHouse */

    public DataSource clickHouseDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    /** JdbcTemplate для простых запросов (плейсхолдеры '?') */

    public JdbcTemplate clickHouseJdbcTemplate(DataSource clickHouseDataSource) {
        return new JdbcTemplate(clickHouseDataSource);
    }

    /** NamedParameterJdbcTemplate для Spring Data JDBC (':param') */

    public NamedParameterJdbcTemplate clickHouseNamedParamTemplate(DataSource clickHouseDataSource) {
        return new NamedParameterJdbcTemplate(clickHouseDataSource);
    }
}
