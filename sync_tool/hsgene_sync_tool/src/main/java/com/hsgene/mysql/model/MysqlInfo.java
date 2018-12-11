package com.hsgene.mysql.model;

import com.hsgene.model.DatabaseInfo;

/**
 * @Author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 9:59 2017/11/6
 * @Modified By:
 */
public class MysqlInfo extends DatabaseInfo {

    private String database;
    private String tables;
    private Boolean useUnicode;
    private String characterEncoding;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public Boolean getUseUnicode() {
        return useUnicode;
    }

    public void setUseUnicode(Boolean useUnicode) {
        this.useUnicode = useUnicode;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String toString() {
        return "MysqlInfo{" +
            "host='" + super.getHost() + '\'' +
            ", port=" + super.getPort() +
            ", database='" + database + '\'' +
            ", tables='" + tables + '\'' +
            ", useUnicode=" + useUnicode +
            ", characterEncoding='" + characterEncoding + '\'' +
            ", user='" + super.getUser() + '\'' +
            ", password='" + super.getPassword() + '\'' +
            '}';
    }
}
