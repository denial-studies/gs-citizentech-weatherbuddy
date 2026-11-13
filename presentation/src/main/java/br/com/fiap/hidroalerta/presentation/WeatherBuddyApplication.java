package br.com.fiap.hidroalerta.presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "br.com.fiap.hidroalerta")
@EnableJpaRepositories(basePackages = "br.com.fiap.hidroalerta.infrastructure.repository")
@EntityScan(basePackages = "br.com.fiap.hidroalerta.infrastructure.entity")
@EnableScheduling
public class WeatherBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherBuddyApplication.class, args);
    }
}
