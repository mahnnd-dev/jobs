//package vn.ndm.data.transfer.service.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class ManagerJob {
//
//    private final JobImport jobImport;
//    private final ThreadPoolTaskExecutor executor;
//
//    @Autowired
//    public ManagerJob(JobImport jobImport, ThreadPoolTaskExecutor executor) {
//        this.jobImport = jobImport;
//        this.executor = executor;
//    }
//
////    @PostConstruct
////    public void init() {
////        try {
////            executor.execute(jobImport);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }
//}
