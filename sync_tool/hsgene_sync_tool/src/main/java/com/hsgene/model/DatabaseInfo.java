package com.hsgene.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:23 2017/10/19
 * @Modified By:
 */
public class DatabaseInfo {

    private String host;
    private int port;
    private String user;
    private String password;
    private String snapshotMysqlUrl;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSnapshotMysqlUrl() {
        return snapshotMysqlUrl;
    }

    public void setSnapshotMysqlUrl(String snapshotMysqlUrl) {
        this.snapshotMysqlUrl = snapshotMysqlUrl;
    }

    @Override
    public String toString() {
        return "DatabaseInfo{" +
               "host='" + host + '\'' +
               ", port=" + port +
               ", user='" + user + '\'' +
               ", password='" + password + '\'' +
               ", snapshotMysqlUrl='" + snapshotMysqlUrl + '\'' +
               '}';
    }
}
