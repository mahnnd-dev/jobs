package dev.m.config;

import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BeanConfig {
    @Value("${job.thread-pool.core-pool-size}")
    int corePoolSize;
    @Value("${job.thread-pool.max-pool-size}")
    int maxPoolSize;
    @Value("${job.thread-pool.queue-capacity}")
    int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize); // Số lượng thread tối thiểu trong pool
        executor.setMaxPoolSize(maxPoolSize);  // Số lượng thread tối đa trong pool
        executor.setQueueCapacity(queueCapacity); // Số lượng tác vụ trong hàng đợi đợi xử lý
        return executor;
    }

    @Bean
    public TelnetClient getTelnetClient() {
        return new TelnetClient();
    }
}
