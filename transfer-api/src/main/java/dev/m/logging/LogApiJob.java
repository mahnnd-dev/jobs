package dev.m.logging;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import dev.m.obj.LogApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

@Component
public class LogApiJob extends AbstractJobProcessLog {
    private final Logger logger = LoggerFactory.getLogger(AbstractJobProcessLog.class);
    private final Gson gson;

    @Value("${logging.job.sql-insert}")
    private String sql;

    @Autowired
    public LogApiJob(@Qualifier("db-log") HikariDataSource ds, Gson gson) {
        super(ds);
        this.gson = gson;
    }

    @Override
    public void processData(String data) {
        try {
            // lấy pre từ cha
            PreparedStatement pstmt = getPstmt();
            LogApi logApi = gson.fromJson(data, LogApi.class);
            pstmt.setString(1, logApi.getRequest());
            pstmt.setString(2, logApi.getResponse());
            pstmt.setInt(3, logApi.getStatus());
            pstmt.setString(4, logApi.getMsg());
            pstmt.setString(5, logApi.getChannel());
            pstmt.setString(6, logApi.getLogDate());
            pstmt.setLong(7, logApi.getTimeProcess());
            pstmt.setString(8, logApi.getAccPartner());
            pstmt.setLong(9, logApi.getReqId());
            pstmt.setString(10, logApi.getMsisdn());
            pstmt.setString(11, logApi.getAccChange());
            pstmt.addBatch();
            // đếm số bản ghi
            incrementCount();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while processing data: {}", e.getMessage());
        }
    }

    @Override
    public String getSql() {
        return sql;
    }
}
