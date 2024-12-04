package dev.m;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ImportResource;
@ImportResource("config/beans.xml")
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
public class JobDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobDatabaseApplication.class, args);
    }

}
