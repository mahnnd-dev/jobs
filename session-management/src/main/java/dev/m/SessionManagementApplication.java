package dev.m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SessionManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SessionManagementApplication.class, args);
    }

}
