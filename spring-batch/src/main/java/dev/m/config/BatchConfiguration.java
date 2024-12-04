package dev.m.config;

import dev.m.service.step.FileReader;
import dev.m.service.step.FileWriter;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import dev.m.service.step.FileProcess;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public FileReader reader;
    @Autowired
    public FileProcess process;
    @Autowired
    public FileWriter writer;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Order, Order>chunk(10)
                .reader(reader)
                .processor(process)
                .writer(writer)
                .taskExecutor(taskExecutor()) // Sử dụng taskExecutor
                .throttleLimit(10) // Giới hạn số lượng luồng đồng thời
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}