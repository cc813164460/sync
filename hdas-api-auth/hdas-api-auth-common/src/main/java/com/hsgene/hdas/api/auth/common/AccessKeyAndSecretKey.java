package com.hsgene.hdas.api.auth.common;

import java.io.Serializable;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.domain
 * @author: maodi
 * @createDate: 2018/9/26 17:44
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class AccessKeyAndSecretKey implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;

    private String accessKey;
    private String secretKey;

    public AccessKeyAndSecretKey() {}

    public AccessKeyAndSecretKey(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "AccessKeyAndSecretKey{" +
               "accessKey='" + accessKey + '\'' +
               ", secretKey='" + secretKey + '\'' +
               '}';
    }
}
