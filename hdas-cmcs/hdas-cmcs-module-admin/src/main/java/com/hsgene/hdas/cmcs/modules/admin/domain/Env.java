package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description: 环境实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:13
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Env implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String name;
    private Timestamp createDateTime;
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

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Env{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", createDateTime=" + createDateTime +
               ", description='" + description + '\'' +
               '}';
    }

}
