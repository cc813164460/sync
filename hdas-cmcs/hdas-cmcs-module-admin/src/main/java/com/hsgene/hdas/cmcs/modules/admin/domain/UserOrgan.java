package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 人员部门关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:18
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class UserOrgan implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long userId;
    private long organId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrganId() {
        return organId;
    }

    public void setOrganId(long organId) {
        this.organId = organId;
    }

    @Override
    public String toString() {
        return "UserOrgan{" +
               "id=" + id +
               ", userId=" + userId +
               ", organId=" + organId +
               '}';
    }

}
