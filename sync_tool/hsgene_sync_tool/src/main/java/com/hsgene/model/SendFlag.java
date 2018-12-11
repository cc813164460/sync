package com.hsgene.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 15:02 2017/11/15
 * @Modified By:
 */
public class SendFlag {
    private TargetInfo targetInfo;
    private String url;
    private String database;
    private String table;
    private String type;
    private AddDataConfig addDataConfig;

    public TargetInfo getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AddDataConfig getAddDataConfig() {
        return addDataConfig;
    }

    public void setAddDataConfig(AddDataConfig addDataConfig) {
        this.addDataConfig = addDataConfig;
    }

    @Override
    public String toString() {
        return "SendFlag{" +
               "targetInfo='" + targetInfo + '\'' +
               ", url='" + url + '\'' +
               ", database='" + database + '\'' +
               ", table='" + table + '\'' +
               ", type='" + type + '\'' +
               ", addDataConfig=" + addDataConfig +
               '}';
    }
}
