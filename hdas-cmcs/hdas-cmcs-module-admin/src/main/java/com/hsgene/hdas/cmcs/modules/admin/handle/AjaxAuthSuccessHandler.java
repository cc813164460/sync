package com.hsgene.hdas.cmcs.modules.admin.handle;

import com.hsgene.hdas.cmcs.modules.admin.domain.Constant;
import com.hsgene.hdas.cmcs.modules.admin.dto.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
@Slf4j
@Component
public class AjaxAuthSuccessHandler implements AuthenticationSuccessHandler, InitializingBean {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication
            authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        String path = request.getContextPath();
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
                          path + "/";
        request.getSession().setAttribute(Constant.SESSION_KEY, username);
        response.sendRedirect(basePath);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
