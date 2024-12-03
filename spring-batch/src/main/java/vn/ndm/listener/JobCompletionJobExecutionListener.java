package vn.ndm.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobCompletionJobExecutionListener implements JobExecutionListener {


    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("#BeforeJob");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("#AfterJob");
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("### JOB FINISHED ###");
            log.info("JobName: {}, JobId: {}, Status: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobId(), jobExecution.getStatus());
        }
    }
}

