package ru.vsu.cs.platon.docs.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(
        /*
         * Extend basePackages to include both:
         * 1) ru.vsu.cs.platon.docs.repository.postgres
         * 2) ru.vsu.cs.platon.docs.repository.clickhouse
         *
         * This ensures Spring Data JPA creates beans for repositories in both packages.
         */
        basePackages = {
                "ru.vsu.cs.platon.docs.repository.postgres",
                "ru.vsu.cs.platon.docs.repository.clickhouse"
        },
        entityManagerFactoryRef = "postgresEntityManager",
        transactionManagerRef = "postgresTransactionManager"
)
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
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .url(databaseUrl)
                .username(databaseUsername)
                .password(databasePassword)
                .driverClassName(driverClassName)
                .build();
    }

    @Primary
    @Bean(name = "postgresEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource primaryDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(primaryDataSource);

        // Scan both your main model package and the clickhouse-package entities
        em.setPackagesToScan(
                "ru.vsu.cs.platon.docs.model",
                "ru.vsu.cs.platon.docs.model.clickhouse"
        );

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        // We use Flyway to manage schema, so disable Hibernate DDL
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        em.setJpaProperties(properties);

        return em;
    }

    @Primary
    @Bean(name = "postgresTransactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory postgresEntityManager) {
        return new JpaTransactionManager(postgresEntityManager);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource primaryDataSource) {
        return new JdbcTemplate(primaryDataSource);
    }
}
