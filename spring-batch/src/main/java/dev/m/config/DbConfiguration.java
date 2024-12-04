package dev.m.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Primary
@Configuration
public class DbConfiguration {
    @ConditionalOnProperty(prefix = "spring", name = "datasource.jdbc-url")
    @Bean(name = "dbprimary")
    @ConfigurationProperties("spring.datasource")
    @Primary
    public HikariDataSource primaryDataSource() {
        return (HikariDataSource) DataSourceBuilder.create().build();
    }

    @ConditionalOnProperty(prefix = "spring", name = "datasource-log.jdbc-url")
    @Bean(name = "db-log")
    @ConfigurationProperties("spring.datasource-log")
    public HikariDataSource logDataSource() {
        return (HikariDataSource) DataSourceBuilder.create().build();
    }
}