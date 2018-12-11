package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 环境项目关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/5/30 10:33
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class EnvModuleProduct implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long envId;
    private long moduleId;
    private long productId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEnvId() {
        return envId;
    }

    public void setEnvId(long envId) {
        this.envId = envId;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "EnvModule{" +
               "id=" + id +
               ", envId=" + envId +
               ", moduleId=" + moduleId +
               ", productId=" + productId +
               '}';
    }

}
