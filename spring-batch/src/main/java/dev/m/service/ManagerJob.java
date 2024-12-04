package dev.m.service;

import dev.m.service.impl.DatabaseStatsToolImpl;
import dev.m.service.impl.ExportDLLImpl;
import dev.m.service.impl.GenerateDataImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class ManagerJob {

    private final ExportDLLImpl exportDLL;
    private final GenerateDataImpl generateData;
    private final DatabaseStatsToolImpl statsTool;
    private final JobRegistry jobRegistry;

    @Autowired
    public ManagerJob(ExportDLLImpl exportDLL, GenerateDataImpl generateData, DatabaseStatsToolImpl statsTool, JobRegistry jobRegistry) {
        this.exportDLL = exportDLL;
        this.generateData = generateData;
        this.statsTool = statsTool;
        this.jobRegistry = jobRegistry;
    }

    @PostConstruct
    public void setJobRegistry() throws DuplicateJobException {
        jobRegistry.register(exportDLL);
        jobRegistry.register(generateData);
        jobRegistry.register(statsTool);
    }
}
