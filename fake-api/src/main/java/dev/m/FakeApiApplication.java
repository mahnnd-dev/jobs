package dev.m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FakeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FakeApiApplication.class, args);
    }
}
