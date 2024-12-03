package dev.m.anotation;

import dev.m.exception.ApiException;
import dev.m.obj.RequestAopModal;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Aspect
@Component
public class ValidationAspect {

    @Pointcut("execution(* dev.m.controller.*.*(..))")
    public void controllerMethods() {
    }

    // bỏ cảnh báo ép kiểu
    @SuppressWarnings("unchecked")
    @Before("controllerMethods()")
    public void validateParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Map) {
                Map<String, String> params = (Map<String, String>) arg;
                validateRequiredParams(params);
            }
            if (arg instanceof RequestAopModal) {
                RequestAopModal params = (RequestAopModal) arg;
                validateRequiredObject(params);
            }
        }
    }

    private void validateRequiredParams(Map<String, String> params) {
        if (!params.containsKey("user") || params.get("user").isEmpty()) {
            throw new ApiException("100", "Username is required");
        }
        if (!params.containsKey("pass") || params.get("pass").isEmpty()) {
            throw new ApiException("110", "Password is required");
        }
    }

    private void validateRequiredObject(RequestAopModal params) {
        if (params.getUserName().isEmpty()) {
            throw new ApiException("100", "Username is required");
        }
        if (params.getPassword().isEmpty()) {
            throw new ApiException("110", "Password is required");
        }
    }
}
