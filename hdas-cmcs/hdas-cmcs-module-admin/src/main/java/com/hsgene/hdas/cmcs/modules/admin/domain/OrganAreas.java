package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @description: 部门分布组关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/8 17:03
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class OrganAreas implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String organName;
    private long[] areaIds;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public long[] getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(long[] areaIds) {
        this.areaIds = areaIds;
    }

    @Override
    public String toString() {
        return "OrganAreas{" +
               "id=" + id +
               ", organName='" + organName + '\'' +
               ", areaIds=" + Arrays.toString(areaIds) +
               '}';
    }

}
