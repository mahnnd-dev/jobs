package dev.m.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class ScheduleJob {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    JobOperator jobOperator;

    @Value("${detection.module.type-code}")
    private String type;

    @Autowired
    public ScheduleJob(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }

    @Scheduled(fixedDelayString = "30000")
    public void perform() throws Exception {
        for (String jobName : jobRegistry.getJobNames()) {
            jobOperator.startNextInstance(jobName);
            log.info("Job name {} Started at :{}", jobName, new Date());
            JobParameters param = new JobParametersBuilder().addString(jobName, String.valueOf(System.currentTimeMillis()))
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(jobRegistry.getJob(jobName), param);
            log.info("Job finished with status :" + execution.getStatus());
        }
    }
}
