package vn.ndm.session.management.service.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import vn.ndm.session.management.contract.AuthenticationService;
import vn.ndm.session.management.obj.AuthUser;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class SessionExpire {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    // config
    int minute = 60 * 1000;
    @Value("${session-management.session-time-expire}")
    private long sessionTimeOut;

    private final AuthenticationService service;
    private final Map<String, AuthUser> hashMapAll;
    private final BlockingQueue<AuthUser> blockingQueueFree;
    private final ThreadPoolTaskExecutor executorService;

    @Autowired
    public SessionExpire(@Qualifier("loginService") AuthenticationService service, @Qualifier("map-all-session") ConcurrentMap<String, AuthUser> hashMapAll, @Qualifier("queue-all-session") BlockingQueue<AuthUser> blockingQueueFree, ThreadPoolTaskExecutor executorService) {
        this.service = service;
        this.hashMapAll = hashMapAll;
        this.blockingQueueFree = blockingQueueFree;
        this.executorService = executorService;
    }

    @Scheduled(fixedDelayString = "${session-management.fixe-delay}")
    public void run() {
        logger.info("#SessionExpire start");
        try {
            blockingQueueFree.forEach(user -> {
                executorService.execute(() -> doRemoveSessionFromQueue(user));
            });
            hashMapAll.entrySet().forEach(entry -> {
                executorService.execute(() -> doRemoveSessionFromMap(entry));
            });
            statusStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doRemoveSessionFromQueue(AuthUser user) {
        synchronized (blockingQueueFree) {
            try {
                if (user != null && user.getTimeOut() > sessionTimeOut) {
                    boolean removed = blockingQueueFree.remove(user);
                    if (removed) {
                        logger.info("doRemoveSessionFromQueue to remove user from queue success: {}", user.getSessionId());
                    } else {
                        logger.warn("Failed to remove user from queue: {}", user.getSessionId());
                    }
                }
            } catch (Exception e) {
                logger.info("Exception doRemoveSessionFromQueue: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void statusStatistics() {
        // Thực hiện thống kê
        Map<Integer, Long> statusCount = hashMapAll.values().stream()
                .filter(user -> user.getStatus() == 1 || user.getStatus() == 0 || user.getStatus() == 2)
                .collect(Collectors.groupingBy(AuthUser::getStatus, Collectors.counting()));
        // In kết quả thống kê
        logger.info("UnUse status={}, Use status={}, Expire status={}"
                , statusCount.getOrDefault(0, 0L)
                , statusCount.getOrDefault(1, 0L)
                , statusCount.getOrDefault(2, 0L));
    }

    public void doRemoveSessionFromMap(Map.Entry<String, AuthUser> entry) {
        synchronized (hashMapAll) {
            try {
                AuthUser user = entry.getValue();
                if (user != null) {
                    if ((user.getTimeOut() > sessionTimeOut + 3L * minute)) {
                        user.setStatus(2);
                        AuthUser authUser = hashMapAll.remove(entry.getKey());
                        if (authUser != null) {
                            logger.info("doRemoveSessionFromMap to remove user from map success: {}", user.getSessionId());
                        } else {
                            logger.warn("Failed to remove user from hashMapAll: {}", user.getSessionId());
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("Exception doRemoveSessionFromMap: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
