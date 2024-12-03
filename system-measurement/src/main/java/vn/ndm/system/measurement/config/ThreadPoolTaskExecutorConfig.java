package vn.ndm.system.measurement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolTaskExecutorConfig {

    @Value("${system-measurement.thread-pool.core-pool-size}")
    private int corePoolSize;
    @Value("${system-measurement.thread-pool.max-pool-size}")
    private int maxPoolSize;
    @Value("${system-measurement.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize); // Số lượng thread tối thiểu trong pool
        executor.setMaxPoolSize(maxPoolSize);  // Số lượng thread tối đa trong pool
        executor.setQueueCapacity(queueCapacity); // Số lượng tác vụ trong hàng đợi đợi xử lý
        return executor;
    }
}
