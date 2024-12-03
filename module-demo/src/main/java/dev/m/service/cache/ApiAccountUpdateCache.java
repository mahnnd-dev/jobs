package dev.m.service.cache;

import dev.m.obj.ApiAccountUpdate;
import dev.m.service.impl.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Service
public class ApiAccountUpdateCache extends AbstractCacheService<ApiAccountUpdate> {
    private final DBService dbService;

    @Autowired
    public ApiAccountUpdateCache(DBService dbService) {
        this.dbService = dbService;
    }

    @Scheduled(fixedDelayString = "${app.sync.time}")
    public void scheduledSync() {
        cacheDataSync();
    }

    @Override
    protected ConcurrentMap<String, ApiAccountUpdate> fetchDataFromDB() {
        return dbService.getAllApiAccountUpdate();
    }

}
