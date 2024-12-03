package dev.m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
public class MP3LyricsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MP3LyricsApiApplication.class, args);
    }

}
