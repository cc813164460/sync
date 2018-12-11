package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description: 环境项目组关系实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 16:09
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class EnvModulesProducts implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String name;
    private Map<Long, List<Long>> productModuleMap;
    private String description;

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

    public Map<Long, List<Long>> getProductModuleMap() {
        return productModuleMap;
    }

    public void setProductModuleMap(Map<Long, List<Long>> productModuleMap) {
        this.productModuleMap = productModuleMap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EnvModulesProducts{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", productModuleMap=" + productModuleMap +
               ", description='" + description + '\'' +
               '}';
    }

}
