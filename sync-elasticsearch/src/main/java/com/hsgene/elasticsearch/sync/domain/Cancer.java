package com.hsgene.elasticsearch.sync.domain;

import java.io.Serializable;

/**
 * @description: 疾病
 * @projectName: hdas-geneshop-server
 * @package: com.hsgene.product.dto
 * @author: maodi
 * @createDate: 2018/10/29 10:18
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Cancer implements Serializable {

    private static final long serialVersionUID = -7254549302516956596L;

    public Cancer(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    /**
     * 疾病名称id
     */
    private String id;

    /**
     * 疾病名称
     */
    private String name;

    /**
     * 疾病分类
     */
    private String category;

    public Cancer() {
    }

    public Cancer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
