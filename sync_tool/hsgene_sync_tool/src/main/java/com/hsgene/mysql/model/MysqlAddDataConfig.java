package com.hsgene.mysql.model;


import com.hsgene.model.AddDataConfig;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 13:56 2017/10/23
 * @Modified By:
 */
public class MysqlAddDataConfig extends AddDataConfig {

    private String endLogPathAndName;
    private int endLogPos;
    private String endLogName;
    private String endLogPath;
    private long ts;



    public String getEndLogPathAndName() {
        return endLogPathAndName;
    }

    public void setEndLogPathAndName(String endLogPathAndName) {
        if (endLogPathAndName != null && endLogPathAndName.startsWith("\"") && endLogPathAndName.endsWith("\"")) {
            endLogPathAndName.substring(1, endLogPathAndName.length() - 1);
        } else {
            this.endLogPathAndName = endLogPathAndName;
        }
    }

    public int getEndLogPos() {
        return endLogPos;
    }

    public void setEndLogPos(int endLogPos) {
        this.endLogPos = endLogPos;
    }

    public String getEndLogName() {
        if (endLogName == null) {
            if (endLogPathAndName != null) {
                if (this.endLogPathAndName.lastIndexOf("\\") > this.endLogPathAndName.lastIndexOf("/")) {
                    return this.endLogPathAndName.substring(this.endLogPathAndName.lastIndexOf("\\") + 1, this
                        .endLogPathAndName.length());
                } else {
                    return this.endLogPathAndName.substring(this.endLogPathAndName.lastIndexOf("/") + 1, this
                        .endLogPathAndName.length());
                }
            } else {
                return endLogName;
            }
        } else {
            if (endLogName.startsWith("\"") && endLogName.endsWith("\"")) {
                endLogName.substring(1, endLogName.length() - 1);
            }
            return endLogName;
        }
    }

    public void setEndLogName(String endLogName) {
        this.endLogName = endLogName;
    }

    public String getEndLogPath() {
        if (endLogPath == null) {
            if (endLogPathAndName != null) {
                if (this.endLogPathAndName.lastIndexOf("\\") > this.endLogPathAndName.lastIndexOf("/")) {
                    return this.endLogPathAndName.substring(0, this.endLogPathAndName.lastIndexOf("\\") + 1);
                } else {
                    return this.endLogPathAndName.substring(0, this.endLogPathAndName.lastIndexOf("/") + 1);
                }
            } else {
                return endLogPath;
            }
        } else {
            if (endLogPath.startsWith("\"") && endLogPath.endsWith("\"")) {
                endLogPath.substring(1, endLogPath.length() - 1);
            }
            return endLogPath;
        }

    }

    public void setEndLogPath(String endLogPath) {
        this.endLogPath = endLogPath;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "MysqlAddDataConfig{" +
               "canalZkServers='" + canalZkServers + '\'' +
               "endLogPathAndName='" + endLogPathAndName + '\'' +
               ", endLogPos=" + endLogPos +
               ", endLogName='" + endLogName + '\'' +
               ", endLogPath='" + endLogPath + '\'' +
               ", ts='" + ts + '\'' +
               ", canalDestination='" + canalDestination + '\'' +
               ", canalHost='" + canalHost + '\'' +
               ", canalPort='" + canalPort + '\'' +
               '}';
    }
}
