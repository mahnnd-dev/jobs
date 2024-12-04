package dev.m.service.v2;

import dev.m.contract.AuthenticationService;
import dev.m.obj.AuthUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

@Service
public class SessionManagement {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    //    config
    @Value("${session-management.int-session}")
    private int numberInitSession;
    @Value("${session-management.min-session}")
    private int minSession;
    @Value("${session-management.session-alive}")
    private long sessionAlive;

    private final AuthenticationService service;
    private final ConcurrentMap<String, AuthUser> hashMapAll;
    private final BlockingQueue<AuthUser> blockingQueueFree;

    @Autowired
    public SessionManagement(@Qualifier("loginService") AuthenticationService service, @Qualifier("map-all-session") ConcurrentMap<String, AuthUser> hashMapAll, @Qualifier("queue-all-session") BlockingQueue<AuthUser> blockingQueueFree) {
        this.service = service;
        this.hashMapAll = hashMapAll;
        this.blockingQueueFree = blockingQueueFree;

    }

    @Scheduled(fixedDelayString = "${session-management.fixe-delay}")
    public void managerSession() {
        try {
            logger.info("#Start ManagerSession");
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String date = sdf.format(d);
            int n = keepAliveAllSessionNew();
            logger.info("===> OK End managerSession All Session : {}, Free Session: {}, NumberKeep: {}, Date Time: {}", hashMapAll.size(), blockingQueueFree.size(), n, date);
        } catch (Exception e) {
            logger.info("Exception managerSession: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void initSession() {
        logger.info("==> Init initSession");
        int n = 0;
        try {
            if (numberInitSession > 0) {
                for (int i = 0; i < numberInitSession; i++) {
                    AuthUser con = newSession();
                    if (con != null) {
                        n++;
                    }
                }
            }
            logger.info("==> OK Init session All Session {} , Free Session: {} , numFree: {}", hashMapAll.size(), blockingQueueFree.size(), n);
        } catch (Exception e) {
            logger.info("Exception initSession: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private int keepAliveAllSessionNew() {
        int n = 0;
        try {
            List<AuthUser> expiredSessions = new ArrayList<>();
            for (AuthUser obj : blockingQueueFree) {
                if (obj.getStatus() != 0 && obj.getTimeOut() > sessionAlive) {
                    expiredSessions.add(obj);
                }
            }
            for (AuthUser obj : expiredSessions) {
                boolean check = blockingQueueFree.remove(obj);
                if (!check)
                    logger.info("Remove {} false", obj);
                obj.setStatus(1);
                boolean is = service.keepAlive(obj);
                if (is) {
                    n++;
                    blockingQueueFree.add(obj);
                }
            }

            int missingSessions = Math.max(0, minSession - blockingQueueFree.size());
            logger.info("#Init session: {}", missingSessions);
            for (int i = 0; i < missingSessions; i++) {
                AuthUser obj = newSession();
                if (obj != null) {
                    n++;
                }
            }
            // del cache expired Sessions
            expiredSessions.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;
    }

    // Phương thức newSession tạo một đối tượng AuthenticatedUser mới
    public synchronized AuthUser newSession() {
        // Tạo đối tượng mới
        AuthUser objectNew = null;
        try {
            // Gọi đến phương thức login() trong service để tạo một đối tượng AuthenticatedUser
            objectNew = service.login();
            // Nếu đối tượng tạo thành công
            if (objectNew != null) {
                storeNewCon(objectNew);
            }
            // Trả về đối tượng mới
            return objectNew;
        } catch (Exception e) {
            // Nếu có lỗi, ghi log lỗi
            logger.info("Exception newSession: {}", e.getMessage());
            return null;
        }
        // Trả về null nếu có lỗi
    }

    private void storeNewCon(AuthUser con) {
        if (con == null) {
            return;
        }
        synchronized (this) {
            try {
                hashMapAll.put(con.getSessionId(), con);
                blockingQueueFree.add(con);
            } catch (Exception e) {
                logger.info("Exception storeNewCon: {}", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
