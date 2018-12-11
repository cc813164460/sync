package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: app实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class App implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id = -1;
    private String ip;
    private String hostname;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "App{" +
               "id=" + id +
               ", ip='" + ip + '\'' +
               ", hostname='" + hostname + '\'' +
               '}';
    }

}
