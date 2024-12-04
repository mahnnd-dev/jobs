package dev.m.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.m.itf.JobProcess;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class JobGateWay implements Runnable {
    private final ConcurrentHashMap<String, JobProcess> mapJob = new ConcurrentHashMap<>();
    @Autowired
//    @Qualifier("jobs")
//    Map<String, Map<String, Object>> jobs;


    // 1 minute/lần
//    @Scheduled(fixedDelay = 10000)
//    public void initJob() {
//        for (Map.Entry<String, Map<String, Object>> entry : jobs.entrySet()) {
//            log.info("Key = {}, Value = {}", entry.getKey(), entry.getValue());
//            JobProcess jobProcess = (JobProcess) entry.getValue();
//            addNewJob(jobProcess.getNameJob(), jobProcess);
//
//        }
//    }

    public Map<String, Map<String, Object>> loadJob() {
        Map<String, Map<String, Object>> mapMap = new HashMap<>();
        try{
            File xmlFile = new File("config/job.xml"); // Đường dẫn đến tệp tin XML
            XmlMapper xmlMapper = new XmlMapper();
            List<Map<String, Object>> jobs = xmlMapper.readValue(xmlFile, new TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> job : jobs) {
                String name = (String) job.get("name");
                Map<String, Object> details = (Map<String, Object>) job.get("details");
                mapMap.put(name, details);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapMap;
    }

    public int getNumJobRuning() {
        int total = 0;
        try{
            Enumeration<JobProcess> e = mapJob.elements();
            while (e.hasMoreElements()) {
                JobProcess job = e.nextElement();
                if (job == null) continue;
                if (job.isWorking()) {
                    total++;
                }else{
                    //Destroy it:
                    setStopJob(job.getNameJob());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    public void addNewJob(String key, JobProcess job) {
        try{
            //Kiem tra key da ton tai:
            if (mapJob.get(key) != null) {
                return;
            }
            mapJob.put(key, job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setStopJob(String key) {
        try{
            JobProcess job = mapJob.remove(key);
            if (job != null) {
                job.stopJob();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
