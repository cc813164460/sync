package com.hsgene.hdas.cmcs.modules.admin.domain;

import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;

import java.io.Serializable;
import java.util.*;

/**
 * @description: 实例实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class InstanceInfo implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long resourceId;
    private String key;
    private String value;
    private int instanceTypeId;
    private String comment;
    private int versionId;
    private int lineNum = 0;
    private long itemId = -1;
    private String versionNum;
    private int releaseStatus;
    private long[] appIds;
    private List<Map<String, Object>> resourceIdsListMap;
    private long classId;
    private long envId;
    private int mustChange;

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
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

    public int getInstanceTypeId() {
        return instanceTypeId;
    }

    public void setInstanceTypeId(int instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public int getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(int releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public long[] getAppIds() {
        return appIds;
    }

    public void setAppIds(long[] appIds) {
        this.appIds = appIds;
    }

    public List<Map<String, Object>> getResourceIdsListMap() {
        return resourceIdsListMap;
    }

    public long[] getResourceIds() {
        int size = resourceIdsListMap.size();
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            Map<String, Object> map = resourceIdsListMap.get(i);
            String isSelect = map.get("is_select").toString();
            if ("1".equals(isSelect)) {
                set.add(Long.valueOf(map.get("id").toString()));
            }
        }
        return StringUtil.longSetAsArray(set);
    }

    public void setResourceIdsListMap(List<Map<String, Object>> resourceIdsListMap) {
        this.resourceIdsListMap = resourceIdsListMap;
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

    public int getMustChange() {
        return mustChange;
    }

    public void setMustChange(int mustChange) {
        this.mustChange = mustChange;
    }

    @Override
    public String toString() {
        return "InstanceInfo{" +
               "resourceId=" + resourceId +
               ", key='" + key + '\'' +
               ", value='" + value + '\'' +
               ", instanceTypeId=" + instanceTypeId +
               ", comment='" + comment + '\'' +
               ", versionId=" + versionId +
               ", lineNum=" + lineNum +
               ", itemId=" + itemId +
               ", versionNum='" + versionNum + '\'' +
               ", releaseStatus=" + releaseStatus +
               ", appIds=" + Arrays.toString(appIds) +
               ", resourceIdsListMap=" + resourceIdsListMap +
               ", classId=" + classId +
               ", envId=" + envId +
               ", mustChange=" + mustChange +
               '}';
    }

}
