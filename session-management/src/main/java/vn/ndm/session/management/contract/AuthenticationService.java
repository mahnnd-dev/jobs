package vn.ndm.session.management.contract;

import org.springframework.stereotype.Service;
import vn.ndm.session.management.obj.AuthUser;

@Service
public abstract class AuthenticationService {
    public abstract AuthUser login();

    public abstract void logout(String session);

    public abstract boolean keepAlive(AuthUser user);
}
