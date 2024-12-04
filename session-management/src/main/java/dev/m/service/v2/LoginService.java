package dev.m.service.v2;

import dev.m.obj.AuthUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.m.contract.AuthenticationService;
import dev.m.util.StringRandom;

@Service
public class LoginService extends AuthenticationService {
    protected final Logger logger = LogManager.getLogger(this.getClass());

    private final AuthUser authUser;

    @Autowired
    public LoginService(AuthUser authUser) {
        this.authUser = authUser;
    }

    @Override
    public AuthUser login() {
        authUser.setSessionId(StringRandom.getRandomCode("SS_", 15));
        authUser.setCookie(StringRandom.getRandomCode("COOK_", 10));
        authUser.setStatus(0);
        authUser.setTimeCreate(System.currentTimeMillis());
        return authUser;
    }

    @Override
    public void logout(String session) {

    }

    @Override
    public boolean keepAlive(AuthUser user) {
        logger.info("#KeepAlive success: {},", user);
        try {
            user.setStatus(0);
            user.setTimeCreate(System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
