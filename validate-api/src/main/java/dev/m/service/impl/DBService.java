//package dev.m.service.impl;
//
//import com.zaxxer.hikari.HikariDataSource;
//import dev.m.obj.ApiAccountUpdate;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//@Service
//public class DBService {
//    private static final Logger logger = LogManager.getLogger(DBService.class);
//    private final HikariDataSource ds;
//    @Value("${app.sync.sql-api-account-update}")
//    private String sqlApiAccountUpdate;
//    @Value("${app.sql-insert}")
//    private String sqlInsert;
//    @Value("${app.sql-update}")
//    private String sqlUpdate;
//    @Value("${app.sql-delete}")
//    private String sqlDelete;
//    @Value("${app.sql-info}")
//    private String sqlInfo;
//
//    public DBService(@Qualifier("dbprimary") HikariDataSource ds) {
//        this.ds = ds;
//    }
//
//
//    public ConcurrentMap<String, ApiAccountUpdate> getAllApiAccountUpdate() {
//        ConcurrentMap<String, ApiAccountUpdate> mapLogin = new ConcurrentHashMap<>();
//        try (Connection conn = this.ds.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sqlApiAccountUpdate);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                ApiAccountUpdate sync = new ApiAccountUpdate();
//                sync.setAccount(rs.getString("account"));
//                sync.setAccPartner(rs.getString("acc_partner"));
//                sync.setPassword(rs.getString("password"));
//                sync.setListIp(rs.getString("list_ip"));
//                sync.setPrivateKey(rs.getString("private_key"));
//                sync.setToken(rs.getString("token"));
//                sync.setSysId(rs.getString("sys_id"));
//                sync.setApiSyncId(rs.getString("api_sync_id"));
//                sync.setIsReport(rs.getInt("is_report"));
//                sync.setMaxLength(rs.getInt("max_length"));
//                sync.setTps(rs.getInt("tps"));
//                mapLogin.put(sync.getAccount(), sync);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("<<<<< Exception when execute {}, message: {}", this.getClass().getMethods(), e.getMessage());
//        }
//        return mapLogin;
//    }
//}
//
