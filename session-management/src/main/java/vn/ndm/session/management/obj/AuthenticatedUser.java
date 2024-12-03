package vn.ndm.session.management.obj;

import lombok.Data;

@Data
public class AuthenticatedUser {
    private String sessionId;
    private String cookie;
    private String comment;
    private String status;
    private long timeSession;
    //    true = used, false = unUsed
    private boolean busy = false;
}
