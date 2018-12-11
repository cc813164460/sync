package com.hsgene.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 10:23 2017/10/26
 * @Modified By:
 */
public class UrlInfo {
    private DatabaseInfo databaseInfo;
    private TargetInfo targetInfo;
    private String dataType;
    private String syncType;
    private AddDataConfig addDataConfig;

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public void setDatabaseInfo(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
    }

    public TargetInfo getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public AddDataConfig getAddDataConfig() {
        return addDataConfig;
    }

    public void setAddDataConfig(AddDataConfig addDataConfig) {
        this.addDataConfig = addDataConfig;
    }

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    @Override
    public String toString() {
        return "UrlInfo{" +
               "databaseInfo=" + databaseInfo +
               ", syncType=" + syncType +
               ", targetInfo=" + targetInfo +
               ", dataType='" + dataType + '\'' +
               ", addDataConfig=" + addDataConfig +
               '}';
    }
}
