package dev.m.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import dev.m.obj.FileInfo;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class BeanConfig {
    @Value("${app.jobs.read-data.path}")
    private String path;

    @Bean("map-file")
    public ConcurrentMap<String, FileInfo> getMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean("queue-read")
    public BlockingQueue<String> queueRead() {
        return new ArrayBlockingQueue<>(1000000);
    }

    @Bean("queue-import")
    public BlockingQueue<String> queueImport() {
        return new ArrayBlockingQueue<>(1000000);
    }

    @Bean
    public File getFile() {
        return new File(path);
    }

    @Bean
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
