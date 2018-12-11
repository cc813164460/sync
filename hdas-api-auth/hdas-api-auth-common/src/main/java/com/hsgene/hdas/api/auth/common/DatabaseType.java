package com.hsgene.hdas.api.auth.common;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/26 17:36
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public enum DatabaseType {

    MYSQL(0, "mysql"), REDIS(1, "redis");

    /**
     * 防止字段值被修改，增加的字段也统一final表示常量
     */
    private final int index;
    private final String type;

    DatabaseType(int index, String type) {
        this.index = index;
        this.type = type;
    }

    /**
     * @param index
     * @return com.hsgene.hdas.api.auth.util.DatabaseType
     * @description 根据key获取枚举
     * @author maodi
     * @createDate 2018/9/26 17:40
     */
    public static DatabaseType getEnumByIndex(int index) {
        for (DatabaseType temp : DatabaseType.values()) {
            if (temp.getIndex() == index) {
                return temp;
            }
        }
        return null;
    }

    public int getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }
}
