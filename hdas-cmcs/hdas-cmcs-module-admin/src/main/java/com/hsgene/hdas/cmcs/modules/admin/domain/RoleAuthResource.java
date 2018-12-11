package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 角色权限关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:16
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class RoleAuthResource implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long arId;
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

    public long getArId() {
        return arId;
    }

    public void setArId(long arId) {
        this.arId = arId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "RoleAuth{" +
               "id=" + id +
               ", arId=" + arId +
               ", roleId=" + roleId +
               '}';
    }

}
