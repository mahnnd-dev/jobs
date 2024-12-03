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
import vn.ndm.tasklet.info.DatabaseStatsTool;

@Slf4j
@Configuration
@EnableBatchProcessing
public class DatabaseStatsToolImpl implements JobFactory {
    private final DatabaseStatsTool databaseStatsTool;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobExecutionListener listener;

    public DatabaseStatsToolImpl(DatabaseStatsTool databaseStatsTool,
                                 JobBuilderFactory jobBuilderFactory,
                                 StepBuilderFactory stepBuilderFactory,
                                 JobExecutionListener listener) {
        this.databaseStatsTool = databaseStatsTool;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.listener = listener;
    }

    @Bean
    public Job jobDatabaseStatsTool() {
        return jobBuilderFactory.get(getJobName())
                .listener(listener)
                .start(step1()).build();
    }

    @Override
    public Job createJob() {
        return jobDatabaseStatsTool();
    }

    @Override
    public String getJobName() {
        return "DatabaseStats";
    }

    @Bean
    public Step step1() {
        log.info("step13");
        return stepBuilderFactory.get("step13")
                .tasklet(databaseStatsTool)
                .build();
    }
}
