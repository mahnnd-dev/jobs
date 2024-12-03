package dev.m.service.itf;

import dev.m.obj.RequestModal;
import dev.m.obj.ResponseModel;

import javax.servlet.http.HttpServletRequest;

public interface ApiAppService {
    ResponseModel api(RequestModal request, HttpServletRequest servletRequest);
}
