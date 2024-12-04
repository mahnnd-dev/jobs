//package vn.ndm.data.transfer.service.impl;
//
//import com.zaxxer.hikari.HikariDataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import vn.ndm.data.transfer.logging.AbstractJobProcessLog;
//
//@Service
//public class JobImport extends AbstractJobProcessLog {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Value("${app.jobs.import-data.path}")
//    private String wait;
//    @Value("${app.jobs.import-data.sql}")
//    private String sqlImport;
//
//    public JobImport(@Qualifier("dbprimary") HikariDataSource ds) {
//        super(ds);
//    }
//
//    @Override
//    public String getPathWait() {
//        return wait;
//    }
//
//    @Override
//    public String getFilePattern() {
//        return "*.csv";
//    }
//
//    @Override
//    public void processData(String data) {
////        logger.info(data);
//    }
//
//    @Override
//    public String getSql() {
//        return sqlImport;
//    }
//}
