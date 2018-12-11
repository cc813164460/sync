package com.hsgene.hdas.cmcs.modules.admin.handle;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.handle
 * @author: maodi
 * @createDate: 2018/6/21 15:18
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class AjaxAuthFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String exceptionStr = exception.toString();
        String s;
        if (exceptionStr.indexOf("UsernameNotFoundException") !=-1) {
            s = "您输入的登陆账号有误，请重新填写";
        } else {
            s = "您输入的登陆密码有误，请重新填写";
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, s);
    }

}
