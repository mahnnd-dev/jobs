package vn.ndm.jobdatabase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BeanConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Số lượng thread tối thiểu trong pool
        executor.setMaxPoolSize(100);  // Số lượng thread tối đa trong pool
        executor.setQueueCapacity(100); // Số lượng tác vụ trong hàng đợi đợi xử lý
        return executor;
    }
}
