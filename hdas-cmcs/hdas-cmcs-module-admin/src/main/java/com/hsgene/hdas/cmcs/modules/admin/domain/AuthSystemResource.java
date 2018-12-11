package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 资源实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:16
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class AuthSystemResource implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private int authId;
    private long systemResourceId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    public long getSystemResourceId() {
        return systemResourceId;
    }

    public void setSystemResourceId(long systemResourceId) {
        this.systemResourceId = systemResourceId;
    }

    @Override
    public String toString() {
        return "AuthSystemResource{" +
               "id=" + id +
               ", authId=" + authId +
               ", systemResourceId=" + systemResourceId +
               '}';
    }

}
