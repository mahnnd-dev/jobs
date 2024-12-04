package dev.m.contract;

import dev.m.obj.AuthUser;
import org.springframework.stereotype.Service;

@Service
public abstract class AuthenticationService {
    public abstract AuthUser login();

    public abstract void logout(String session);

    public abstract boolean keepAlive(AuthUser user);
}
