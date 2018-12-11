package com.hsgene.mongodb.model;


import com.hsgene.model.DatabaseInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:25 2017/10/19
 * @Modified By:
 */
public class MongodbInfo extends DatabaseInfo {

    private String authDatabase;
    private String database;
    private String tables;
    private String mechanism = "SCRAM-SHA-1";
    private String oplogDatabase;
    private String mysqlUrl;

    public String getAuthDatabase() {
        return authDatabase;
    }

    public void setAuthDatabase(String authDatabase) {
        this.authDatabase = authDatabase;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTables() {
        return tables;
    }

    public List<String> getTableList() {
        if (tables.length() == 0) {
            return new ArrayList<>();
        }
        return Arrays.asList(tables.replace(" ", "").split(","));
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getOplogDatabase() {
        return oplogDatabase;
    }

    public void setOplogDatabase(String oplogDatabase) {
        this.oplogDatabase = oplogDatabase;
    }

    public String getMysqlUrl() {
        return mysqlUrl;
    }

    public void setMysqlUrl(String mysqlUrl) {
        this.mysqlUrl = mysqlUrl;
    }

    public String getMechanism() {
        return mechanism;
    }

    public void setMechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    @Override
    public String toString() {
        return "MongodbInfo{" +
               "authDatabase='" + authDatabase + '\'' +
               "host='" + super.getHost() + '\'' +
               ", port=" + super.getPort() +
               ", database='" + database + '\'' +
               ", tables='" + tables + '\'' +
               ", oplogDatabase='" + oplogDatabase + '\'' +
               ", mechanism='" + mechanism + '\'' +
               ", user='" + super.getUser() + '\'' +
               ", password='" + super.getPassword() + '\'' +
               ", mysqlUrl='" + mysqlUrl + '\'' +
               ", snapshotMysqlUrl='" + super.getSnapshotMysqlUrl() + '\'' +
               '}';
    }
}
