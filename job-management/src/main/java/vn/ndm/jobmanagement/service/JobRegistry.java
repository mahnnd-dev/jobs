package vn.ndm.jobmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ndm.jobmanagement.object.Job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class JobRegistry {
    private static final Map<String, Job> jobs = new ConcurrentHashMap<>();

    public void register(Job job) {
        jobs.put(job.getName(), job);
    }

    public Job getJob(String name) {
        return jobs.get(name);
    }
}
