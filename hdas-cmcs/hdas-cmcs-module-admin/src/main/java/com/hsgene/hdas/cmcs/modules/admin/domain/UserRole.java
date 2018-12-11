package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 人员角色关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:16
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class UserRole implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long userId;
    private long roleId;

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

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserRole{" +
               "id=" + id +
               ", userId=" + userId +
               ", roleId=" + roleId +
               '}';
    }

}
