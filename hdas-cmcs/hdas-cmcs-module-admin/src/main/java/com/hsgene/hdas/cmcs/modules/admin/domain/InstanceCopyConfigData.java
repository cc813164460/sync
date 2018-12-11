package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description: 实例实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class InstanceCopyConfigData implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private String productModuleEnvId;
    private long resourceId;
    private int versionId;
    private int instanceTypeId;
    private long envId;
    private List<Map<String, Object>> copyConfigList;

    public String getProductModuleEnvId() {
        return productModuleEnvId;
    }

    public void setProductModuleEnvId(String productModuleEnvId) {
        this.productModuleEnvId = productModuleEnvId;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getInstanceTypeId() {
        return instanceTypeId;
    }

    public void setInstanceTypeId(int instanceTypeId) {
        this.instanceTypeId = instanceTypeId;
    }

    public List<Map<String, Object>> getCopyConfigList() {
        return copyConfigList;
    }

    public void setCopyConfigList(List<Map<String, Object>> copyConfigList) {
        this.copyConfigList = copyConfigList;
    }

    public long getEnvId() {
        return envId;
    }

    public void setEnvId(long envId) {
        this.envId = envId;
    }

    @Override
    public String toString() {
        return "InstanceCopyConfigData{" +
               "productModuleEnvId='" + productModuleEnvId + '\'' +
               ", resourceId=" + resourceId +
               ", versionId=" + versionId +
               ", instanceTypeId=" + instanceTypeId +
               ", envId=" + envId +
               ", copyConfigList=" + copyConfigList +
               '}';
    }

}
