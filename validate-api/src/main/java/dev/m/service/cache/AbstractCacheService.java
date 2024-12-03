package dev.m.service.cache;

import dev.m.service.itf.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public abstract class AbstractCacheService<T> implements CacheService<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCacheService.class);
    private final ConcurrentMap<String, T> concurrentMap = new ConcurrentHashMap<>();

    // Phương thức trừu tượng để lấy dữ liệu từ cơ sở dữ liệu
    protected abstract ConcurrentMap<String, T> fetchDataFromDB();

    // Lấy cache hiện tại
    @Override
    public ConcurrentMap<String, T> getCache() {
        return concurrentMap;
    }

    // Lấy giá trị từ cache dựa trên khóa
    public T getValueCache(String key) {
        return concurrentMap.get(key);
    }

    // Đồng bộ hóa dữ liệu cache
    @Override
    public void cacheDataSync() {
        Instant st1 = Instant.now(); // Bắt đầu thời gian
        ConcurrentMap<String, T> mapListUpdate = fetchDataFromDB(); // Lấy dữ liệu mới từ cơ sở dữ liệu
        Instant en1 = Instant.now(); // Kết thúc thời gian
        long time1 = Duration.between(st1, en1).toMillis(); // Tính thời gian truy vấn

        List<String> listRemove = new ArrayList<>(); // Danh sách để loại bỏ các mục không còn tồn tại

        try {
            Instant st = Instant.now(); // Bắt đầu thời gian

            // Loại bỏ các mục không còn tồn tại trong cơ sở dữ liệu mới
            for (Map.Entry<String, T> entryCache : concurrentMap.entrySet()) {
                if (mapListUpdate.get(entryCache.getKey()) == null) {
                    listRemove.add(entryCache.getKey());
                }
            }

            // Cập nhật thông tin mới
            for (Map.Entry<String, T> entry : mapListUpdate.entrySet()) {
                concurrentMap.put(entry.getKey(), entry.getValue());
            }

            // Loại bỏ các mục cũ không còn tồn tại trong dữ liệu mới
            for (String string : listRemove) {
                concurrentMap.remove(string);
            }

            Instant en = Instant.now(); // Kết thúc thời gian
            long time = Duration.between(st, en).toMillis(); // Tính thời gian cập nhật
            logger.debug(">> Sync data success for {} size: {}, time select: {}ms, Time sync: {}ms, object remove: {}", this.getClass().getSimpleName(), concurrentMap.size(), time1, time, listRemove);
        } catch (Exception e) {
            // Log nếu có lỗi xảy ra
            logger.error("Exception during sync Data for {}: {}", this.getClass().getSimpleName(), e.getMessage());
            logger.error(String.valueOf(e));
        }
    }
}
