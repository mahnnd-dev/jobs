package vn.ndm.jobmanagement.module;

import lombok.extern.slf4j.Slf4j;
import vn.ndm.jobmanagement.itf.JobProcess;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class JobClearTrash extends Thread implements JobProcess {
    private boolean isRun = true;
    Map<String, Object> hashMap = new HashMap<>();
    private final ScheduledExecutorService executorService;
    private String name = "JobClearTrash";
    private String _class = "";
    private String active = "";
    private String cronExpression;
    private List<String> paths = null;

    public JobClearTrash() {
        this.executorService = Executors.newScheduledThreadPool(10);
    }


    @Override
    public void setParam(Map<String, Object> param) {
//        log.info("----->Init tham so Module JobClearTrash: {}", param);
        hashMap = param;
        _class = (String) hashMap.get("class");
        active = (String) hashMap.get("active");
        cronExpression = (String) hashMap.get("cronTime");
        Map<String, List<String>> listMap = (Map<String, List<String>>) hashMap.get("paths");
        paths = listMap.get("path");
    }

    @Override
    public void execute() {
//        log.info("Job JobClearTrash start");
        try{
            for (String path : paths) {
                deleteRecursive(new File(path));
            }
            sleep(Long.parseLong(cronExpression));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopJob() {
        try{
            isRun = false;
            this.join(2 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void workJob() {

    }

    @Override
    public String getNameJob() {
        return name;
    }

    @Override
    public boolean isWorking() {
        return isRun;
    }

    public void deleteRecursive(File path) {
        log.info("Cleaning out folder: {}", path.toString());
        try{
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteRecursive(file);
                        Path p = Paths.get(file.getPath());
                        log.info("Delete folder {} status: {}", p, Files.deleteIfExists(p));
                    }else{
                        Path p = Paths.get(file.getPath());
                        log.info("Delete file {} status: {}", p, Files.deleteIfExists(p));
                    }
                }
            }
        } catch (Exception e) {
            log.info("#Error delete file: {}", e.getMessage());
        }
    }
}
