package com.hsgene.hdas.cmcs.modules.admin.security;

import org.springframework.security.access.ConfigAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.security
 * @author: maodi
 * @createDate: 2018/6/22 11:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class AuthConfigAttribute implements ConfigAttribute {

    private final HttpServletRequest httpServletRequest;

    public AuthConfigAttribute(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public String getAttribute() {
        return null;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

}
