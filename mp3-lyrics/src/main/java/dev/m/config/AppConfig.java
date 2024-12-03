
package dev.m.config;

import com.google.gson.Gson;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "dev.m")
public class AppConfig {

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
