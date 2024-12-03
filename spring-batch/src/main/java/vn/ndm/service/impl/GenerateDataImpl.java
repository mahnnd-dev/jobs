package vn.ndm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.ndm.tasklet.genarate.GenerateDataService;

@Slf4j
@Configuration
@EnableBatchProcessing
public class GenerateDataImpl implements JobFactory {
    private final GenerateDataService generateDataService;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobExecutionListener listener;

    public GenerateDataImpl(GenerateDataService generateDataService,
                            JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory,
                            JobExecutionListener listener) {
        this.generateDataService = generateDataService;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.listener = listener;
    }

    @Bean
    public Job jobGenerateDataService() {
        return jobBuilderFactory.get(getJobName())
                .listener(listener)
                .start(step1()).build();
    }

    @Override
    public Job createJob() {
        return jobGenerateDataService();
    }

    @Override
    public String getJobName() {
        return "GenerateData";
    }

    @Bean
    public Step step1() {
        log.info("step12");
        return stepBuilderFactory.get("step12")
                .tasklet(generateDataService)
                .build();
    }
}
