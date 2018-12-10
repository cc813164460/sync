package com.hsgene.elasticsearch.sync.domain;

/**
 * @description:
 * @projectName: sync-elasticsearch
 * @package: com.hsgene.elasticsearch.sync.domain
 * @author: maodi
 * @createDate: 2018/12/6 16:33
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public enum SyncType {

    MYSQL_TO_ELASTICSEARCH(0, "mysql-to-elasticsearch", "mysql同步数据到elasticsearch"),
    ELASTICSEARCH_TO_ELASTICSEARCH(1, "elasticsearch-to-elasticsearch", "elasticsearch同步数据到elasticsearch");

    /**
     * 编号
     */
    private final int code;

    /**
     * 类型
     */
    private final String type;

    /**
     * 解释说明
     */
    private final String description;

    SyncType(int code, String type, String description) {
        this.code = code;
        this.type = type;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
