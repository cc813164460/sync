package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 部门分布关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/8 11:31
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class OrganArea implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long areaId;
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

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public long getOrganId() {
        return organId;
    }

    public void setOrganId(long organId) {
        this.organId = organId;
    }

    @Override
    public String toString() {
        return "OrganArea{" +
               "id=" + id +
               ", areaId=" + areaId +
               ", organId=" + organId +
               '}';
    }

}
