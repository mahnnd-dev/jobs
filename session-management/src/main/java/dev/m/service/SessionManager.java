//package vn.ndm.session.management.service;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import vn.ndm.session.management.contract.AuthenticationService;
//import vn.ndm.session.management.obj.AuthenticatedUser;
//
//import javax.annotation.PostConstruct;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class SessionManager {
//    protected final Logger logger = LogManager.getLogger(this.getClass());
//    //    config
//    @Value("${session-management.max-session}")
//    private int maxSession;
//    @Value("${session-management.time-wait-free}")
//    private int timeWaitFree;
//    @Value("${session-management.int-session}")
//    private int numberInitSession;
//    @Value("${session-management.min-session}")
//    private int minSession;
//    int minute = 60 * 1000;
//    @Value("${session-management.session-time-out}")
//    private long sessionTimeOut;
//    @Value("${session-management.session-alive}")
//    private long sessionAlive;
//
//    private final AuthenticationService service;
//    private final ConcurrentHashMap<String, AuthenticatedUser> hashMapAll = new ConcurrentHashMap<>();
//    private final BlockingQueue<AuthenticatedUser> blockingQueueFree;
//
//    @Autowired
//    public SessionManager(AuthenticationService service, @Qualifier("BlockingQueue") BlockingQueue<AuthenticatedUser> blockingQueueFree) {
//        this.service = service;
//        this.blockingQueueFree = blockingQueueFree;
//    }
//
//    @PostConstruct
//    public void initSession() {
//        logger.info("==> Init initSession");
//        int n = 0;
//        try{
//            if (numberInitSession > 0) {
//                for (int i = 0; i < numberInitSession; i++) {
//                    AuthenticatedUser con = newSession(true);
//                    if (con != null) {
//                        n++;
//                    }
//                }
//            }
//            logger.info("==> OK Init session All Session {} , Free Session: {} , numFree: {}", hashMapAll.size(), blockingQueueFree.size(), n);
//        } catch (Exception e) {
//            logger.info("Exception initSession: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Scheduled(fixedDelayString = "${session-management.fixe-delay}")
//    public void managerSession() {
//        try{
//            logger.info("#Start ManagerSession");
//            Date d = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//            String date = sdf.format(d);
//            long time = System.currentTimeMillis();
//            doRemoveSession(time);
//            int n = keepAliveAllSessionNew(time);
//            logger.info("===> OK End managerSession All Session : {}, Free Session: {}, NumberKeep: {}, Date Time: {}", hashMapAll.size(), blockingQueueFree.size(), n, date);
//        } catch (Exception e) {
//            logger.info("Exception managerSession: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    // Phương thức newSession tạo một đối tượng AuthenticatedUser mới
//    public synchronized AuthenticatedUser newSession(boolean isFree) {
//        // Tạo đối tượng mới
//        AuthenticatedUser objectNew = null;
//        try{
//            // Gọi đến phương thức login() trong service để tạo một đối tượng AuthenticatedUser
//            objectNew = service.login("", "");
//            // Nếu đối tượng tạo thành công
//            if (objectNew != null) {
//                storeNewCon(objectNew, isFree);
//            }
//            // Trả về đối tượng mới
//            return objectNew;
//        } catch (Exception e) {
//            // Nếu có lỗi, ghi log lỗi
//            logger.info("Exception newSession: {}", e.getMessage());
//            return null;
//        }
//        // Trả về null nếu có lỗi
//    }
//
//    public void freeCon(AuthenticatedUser con) {
//        try{
//            if (con != null && con.getSessionId().length() > 0) {
//                con.setBusy(false);
//                con.setTimeSession(System.currentTimeMillis());
//                blockingQueueFree.add(con);
//            }
//        } catch (Exception e) {
//            logger.info("Exception freeCon: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private int keepAliveAllSessionNew(long currentTime) {
//        int n = 0;
//        try{
//            List<AuthenticatedUser> expiredSessions = new ArrayList<>();
//            for (AuthenticatedUser obj : blockingQueueFree) {
//                if (!obj.isBusy() && currentTime - obj.getTimeSession() > sessionAlive) {
//                    expiredSessions.add(obj);
//                }
//            }
//            for (AuthenticatedUser obj : expiredSessions) {
//                boolean check = blockingQueueFree.remove(obj);
//                if (!check)
//                    logger.info("Remove {} false", obj);
//                obj.setBusy(true);
//                AuthenticatedUser is = service.keepAlive(obj.getSessionId());
//                if (is != null) {
//                    n++;
//                    obj.setBusy(false);
//                    obj.setTimeSession(System.currentTimeMillis());
//                    blockingQueueFree.add(obj);
//                }else{
//                    destroySession(obj);
//                }
//            }
//
//            int missingSessions = Math.max(0, minSession - blockingQueueFree.size());
//            for (int i = 0; i < missingSessions; i++) {
//                AuthenticatedUser obj = newSession(true);
//                if (obj != null) {
//                    n++;
//                }
//            }
//            // del cache expired Sessions
//            expiredSessions.clear();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return n;
//    }
//
//    public AuthenticatedUser getAuthenticatedUser() {
//        AuthenticatedUser con = null;
//        try{
//            con = blockingQueueFree.poll();
//            if (con != null) {
//                con.setBusy(true);
//                logger.info("==> OK get Session: {}", con.getSessionId());
//                return con;
//            }
//            if (hashMapAll.size() - maxSession > 0) {
//                logger.info("Max session");
//                con = blockingQueueFree.poll(timeWaitFree, TimeUnit.MILLISECONDS);
//                if (con != null) {
//                    con.setBusy(true);
//                }
//                return con;
//            }else{
//                con = newSession(false);
//                if (con != null) {
//                    con.setBusy(true);
//                }
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            logger.info("Exception getAuthenticatedUser: {}", e.getMessage());
//            e.printStackTrace();
//        }
//        return con;
//    }
//
//    private void storeNewCon(AuthenticatedUser con, boolean isFree) {
//        if (con == null) {
//            return;
//        }
//        synchronized (this) {
//            try{
//                hashMapAll.put(con.getSessionId(), con);
//                if (isFree) {
//                    blockingQueueFree.add(con);
//                }
//            } catch (Exception e) {
//                logger.info("Exception storeNewCon: {}", e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void destroySession(AuthenticatedUser con) {
//        try{
//            if (con != null) {
//                con.setBusy(true);
//                service.logout(con.getSessionId());
//                moveFreeQueue(con);
//                hashMapAll.remove(con.getSessionId());
//            }
//        } catch (Exception e) {
//            logger.info("Exception destroySession: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void moveFreeQueue(AuthenticatedUser con) {
//        try{
//            synchronized (blockingQueueFree) {
//                if (!blockingQueueFree.remove(con)) {
//                    logger.info("Remove error: {}", con.getSessionId());
//                }else{
//                    logger.info("Remove success: {}", con.getSessionId());
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Exception moveFreeQueue: {}", e.getMessage());
//        }
//    }
//
//    public void doRemoveSession(long currentTime) {
//        try{
//            Iterator<AuthenticatedUser> iterator = blockingQueueFree.iterator();
//            while (iterator.hasNext()) {
//                AuthenticatedUser obj = iterator.next();
//                if (obj != null && currentTime - obj.getTimeSession() > sessionTimeOut) {
//                    iterator.remove();
//                    destroySession(obj);
//                }
//            }
//            Iterator<Map.Entry<String, AuthenticatedUser>> hashMapIterator = hashMapAll.entrySet().iterator();
//            while (hashMapIterator.hasNext()) {
//                Map.Entry<String, AuthenticatedUser> entry = hashMapIterator.next();
//                AuthenticatedUser obj = entry.getValue();
//                if (obj != null && (currentTime - obj.getTimeSession() > sessionTimeOut + 3L * minute)) {
//                    hashMapIterator.remove();
//                    destroySession(obj);
//                }
//            }
//        } catch (Exception e) {
//            logger.info("Exception doRemoveSession: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void logoutCon(AuthenticatedUser con) {
//        try{
//            con.setBusy(true);
//            service.logout(con.getSessionId());
//            hashMapAll.remove(con.getSessionId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
//
