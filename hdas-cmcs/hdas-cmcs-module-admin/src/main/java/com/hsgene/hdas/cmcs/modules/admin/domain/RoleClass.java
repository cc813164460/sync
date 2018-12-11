package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/8/15 9:57
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class RoleClass implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long roleId;
    private long classId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "RoleClass{" +
               "id=" + id +
               ", roleId=" + roleId +
               ", classId=" + classId +
               '}';
    }

}
