package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 实例实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Instance implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String name;
    private int isDelete = 0;
    private int instanceTypeId;
    private String resourceId;

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

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getInstanceTypeId() {
        return instanceTypeId;
    }

    public void setInstanceTypeId(int instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }

    @Override
    public String toString() {
        return "Instance{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", isDelete=" + isDelete +
               ", instanceTypeId=" + instanceTypeId +
               ", resourceId='" + resourceId + '\'' +
               '}';
    }

}
