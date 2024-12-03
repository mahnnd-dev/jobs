package vn.ndm.jobmanagement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.ndm.jobmanagement.object.Job;
import vn.ndm.jobmanagement.object.Jobs;
import vn.ndm.jobmanagement.object.Param;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

@Slf4j
@Service
public class LoadFileConfig {

    private final JobRegistry jobRegistry;

    @Autowired
    public LoadFileConfig(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    @Scheduled(fixedDelay = 30000)
    public void loadFile() throws Exception {
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Jobs.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File file = new File("config/job.xml");
            Jobs jobs = (Jobs) unmarshaller.unmarshal(file);
            List<Job> jobList = jobs.getJobs();
            for (Job job : jobList) {
                System.out.println("Tên job: " + job.getName());
                System.out.println("Class job: " + job.getClassJob());
                System.out.println("Trạng thái: " + job.getActive());
                System.out.println("Cron time: " + job.getCronTime());
                for (Param path : job.getParams()) {
                    System.out.println("- " + path.getValue());
                }
            }
            // Đăng ký job
//            jobRegistry.register();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error loadFile xml {}", e.getMessage());
        }
    }
}
