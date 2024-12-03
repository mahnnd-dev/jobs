package vn.ndm.session.management.obj;

import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
public class AuthUser {
    private String sessionId;
    private String cookie;
    private int status;
    private long timeCreate;
    private long timeExpire;

    public long getTimeOut() {
        long currentTime = System.currentTimeMillis();
        return currentTime - timeCreate;
    }
}