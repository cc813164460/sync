package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.util.Set;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/22 16:22
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ConfigAuthUrl implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long resourceId;
    private long authId;
    private Set<String> urlSet;

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getAuthId() {
        return authId;
    }

    public void setAuthId(long authId) {
        this.authId = authId;
    }

    public Set<String> getUrlSet() {
        return urlSet;
    }

    public void setUrlSet(Set<String> urlSet) {
        this.urlSet = urlSet;
    }

    @Override
    public String toString() {
        return "ConfigAuthUrl{" +
               "resourceId=" + resourceId +
               ", authId=" + authId +
               ", urlSet=" + urlSet +
               '}';
    }

}
