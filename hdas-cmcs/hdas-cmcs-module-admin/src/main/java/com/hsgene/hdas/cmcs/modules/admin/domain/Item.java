package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description: app实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String versionNum;
    private long instanceId;
    private String key;
    private String value;
    private String comment;
    private int lineNum;
    private int isDelete = 0;
    private int isPublic;
    private int instanceTypeId;
    private int releaseStatus;
    private long lastUpdateBy;
    private Timestamp lastUpdateTime;
    private int versionId;
    private long classId = 0;
    private long envId;
    private Timestamp updateTime;
    private int mustChange;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(int releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public long getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(long lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getInstanceTypeId() {
        return instanceTypeId;
    }

    public void setInstanceTypeId(int instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }

    public long getClassId() {
        return classId;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public long getEnvId() {
        return envId;
    }

    public void setEnvId(long envId) {
        this.envId = envId;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public int getMustChange() {
        return mustChange;
    }

    public void setMustChange(int mustChange) {
        this.mustChange = mustChange;
    }

    @Override
    public String toString() {
        return "Item{" +
               "id=" + id +
               ", versionNum='" + versionNum + '\'' +
               ", instanceId=" + instanceId +
               ", key='" + key + '\'' +
               ", value='" + value + '\'' +
               ", comment='" + comment + '\'' +
               ", lineNum=" + lineNum +
               ", isDelete=" + isDelete +
               ", isPublic=" + isPublic +
               ", instanceTypeId=" + instanceTypeId +
               ", releaseStatus=" + releaseStatus +
               ", lastUpdateBy=" + lastUpdateBy +
               ", lastUpdateTime=" + lastUpdateTime +
               ", versionId=" + versionId +
               ", classId=" + classId +
               ", envId=" + envId +
               ", updateTime=" + updateTime +
               ", mustChange=" + mustChange +
               '}';
    }

}
