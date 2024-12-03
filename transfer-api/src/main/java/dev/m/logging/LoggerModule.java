package dev.m.logging;

import com.google.gson.Gson;
import dev.m.obj.LogApi;
import dev.m.obj.RequestModal;
import dev.m.obj.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoggerModule {
    private static final Logger logger = LoggerFactory.getLogger(LoggerModule.class);
    private static final Logger logInbound = LoggerFactory.getLogger("log-inbound");
    private final Gson gson;

    @Autowired
    public LoggerModule(Gson gson) {
        this.gson = gson;
    }

    public void writeLoggerApi(RequestModal request, ResponseModel responseModel, String accPartner, String logDate, long time) {
        try {
            LogApi logApi = new LogApi();
            String logApiStr = gson.toJson(logApi);
            logInbound.info(logApiStr);
            logger.info(">> writeLoggerApi Success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception writeLoggerApi {}", e.getMessage());
        }
    }

}
