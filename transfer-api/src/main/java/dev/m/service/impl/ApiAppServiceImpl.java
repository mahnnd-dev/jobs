package dev.m.service.impl;

import dev.m.logging.LoggerModule;
import dev.m.obj.RequestModal;
import dev.m.obj.ResponseModel;
import dev.m.service.cache.ApiAccountUpdateCache;
import dev.m.service.itf.ApiAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ApiAppServiceImpl implements ApiAppService {
    private static final Logger logger = LoggerFactory.getLogger(ApiAppServiceImpl.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final LoggerModule module;
    private final DBService dbService;
    private final ApiAccountUpdateCache apiAccountUpdateCache;


    @Autowired
    public ApiAppServiceImpl(LoggerModule module, DBService dbService, ApiAccountUpdateCache apiAccountUpdateCache) {
        this.module = module;
        this.dbService = dbService;
        this.apiAccountUpdateCache = apiAccountUpdateCache;
    }

    @Override
    public ResponseModel api(RequestModal request, HttpServletRequest servletRequest) {
        // object write log
        Date date = new Date();
        String logDate = sdf.format(date);
        logDate = logDate.replace("?", "//");
        Instant startMs = Instant.now();
        // khoi tao object
        ResponseModel responseModel = new ResponseModel();
        try {
            return responseModel;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error check-info {}", e.getMessage());
            return responseModel;
        } finally {
            // ghi log
            Instant end = Instant.now();
            long time = Duration.between(startMs, end).toMillis();
            logger.info(">> CheckInfo request: {}, response: {}, time: {}ms", request, responseModel, time);

        }
    }

    public ResponseModel validateParam(RequestModal request, HttpServletRequest servletRequest,String...params ) {
        return null;
    }

}
