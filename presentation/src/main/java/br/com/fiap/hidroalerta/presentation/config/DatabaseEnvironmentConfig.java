package br.com.fiap.hidroalerta.presentation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseEnvironmentConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${db-env:false}")
    private String dbEnv;

    @Bean
    public DataSource dataSource() {
        boolean mockAtivo = "true".equalsIgnoreCase(dbEnv)
                || "on".equalsIgnoreCase(dbEnv)
                || "1".equals(dbEnv);

        if (mockAtivo) {
            return DataSourceBuilder.create()
                    .url("jdbc:h2:mem:weatherbuddy;MODE=Oracle;DB_CLOSE_DELAY=-1")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }

        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
