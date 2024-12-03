package com.ndm.fakeapi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamicApiConfig implements WebMvcConfigurer {
    private final ApplicationContext applicationContext;
    @Value("${server.port}")
    private String host;
    @Value("${job.path}")
    private String path;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }

    @Bean
    public ViewControllerRegistry viewControllerRegistry() {
        return new ViewControllerRegistry(applicationContext);
    }

    public void registerEndpoints(ViewControllerRegistry registry) {
        Path directory = Paths.get(path);
        try {
            Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString().replaceAll("\\.[^.]*$", "");
                        registry.addViewController("/" + fileName).setViewName("forward:/api/" + fileName);
                        log.info("EndPoint API - GET/POST: http://localhost:{}/api/{} ", host, fileName);
                    });
        } catch (IOException e) {
            log.error("Error reading files in directory: {}", directory, e);
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void scheduleRegisterEndpoints() {
        log.info(">> Refesh folder {}", new Date());
        registerEndpoints(viewControllerRegistry());
    }
 
}