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
public class SysResource implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private int oneName;
    private long twoName;
    private long threeName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOneName() {
        return oneName;
    }

    public void setOneName(int oneName) {
        this.oneName = oneName;
    }

    public long getTwoName() {
        return twoName;
    }

    public void setTwoName(long twoName) {
        this.twoName = twoName;
    }

    public long getThreeName() {
        return threeName;
    }

    public void setThreeName(long threeName) {
        this.threeName = threeName;
    }

    @Override
    public String toString() {
        return "SysResource{" +
               "id=" + id +
               ", oneName=" + oneName +
               ", twoName=" + twoName +
               ", threeName=" + threeName +
               '}';
    }

}
