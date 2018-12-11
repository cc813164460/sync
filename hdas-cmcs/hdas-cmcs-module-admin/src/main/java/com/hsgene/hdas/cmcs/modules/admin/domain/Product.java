package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 项目实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/5/30 10:33
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String name;
    private long userId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", userId=" + userId +
               ", areaId=" + areaId +
               ", organId=" + organId +
               '}';
    }

}
