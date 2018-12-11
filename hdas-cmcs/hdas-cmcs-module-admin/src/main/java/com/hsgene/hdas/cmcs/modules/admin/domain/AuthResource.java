package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 权限资源关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:16
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class AuthResource implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long authId;
    private long resourceId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthId() {
        return authId;
    }

    public void setAuthId(long authId) {
        this.authId = authId;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "AuthResource{" +
               "id=" + id +
               ", authId=" + authId +
               ", resourceId=" + resourceId +
               '}';
    }

}
