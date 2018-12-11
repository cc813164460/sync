package com.hsgene.hdas.cmcs.modules.admin.dto;

import org.springframework.security.core.GrantedAuthority;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.security
 * @author: maodi
 * @createDate: 2018/6/22 10:18
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class AuthGrantedAuthority implements GrantedAuthority {

    private String permissionUrl;
    private String httpMethod;

    public String getPermissionUrl() {
        return permissionUrl;
    }

    public void setPermissionUrl(String permissionUrl) {
        this.permissionUrl = permissionUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public AuthGrantedAuthority(String permissionUrl, String httpMethod) {
        this.permissionUrl = permissionUrl;
        this.httpMethod = httpMethod;
    }

    @Override
    public String getAuthority() {
        return this.permissionUrl + ";" + this.httpMethod;
    }

}
