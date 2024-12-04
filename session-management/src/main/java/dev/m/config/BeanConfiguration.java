package dev.m.config;

import dev.m.obj.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Configuration
public class BeanConfiguration {
    @Bean("queue-all-session")
    public BlockingQueue<AuthUser> queue(@Value("${session-management.max-session}") int queueSize) {
        return new ArrayBlockingQueue<>(queueSize);
    }

    @Bean("map-all-session")
    public ConcurrentMap<String, AuthUser> getHashMapAll() {
        return new ConcurrentHashMap<>();
    }

    @Bean("login-service")
    public ConcurrentMap<String, AuthUser> logInService() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public AuthUser authenticatedUser() {
        return new AuthUser();
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Số lượng thread tối thiểu trong pool
        executor.setMaxPoolSize(100);  // Số lượng thread tối đa trong pool
        executor.setQueueCapacity(100); // Số lượng tác vụ trong hàng đợi đợi xử lý
        return executor;
    }
}

