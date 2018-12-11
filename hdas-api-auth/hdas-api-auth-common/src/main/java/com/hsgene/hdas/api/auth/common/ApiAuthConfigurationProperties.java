package com.hsgene.hdas.api.auth.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/27 14:28
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@ConfigurationProperties(prefix = "api-auth")
@Data
public class ApiAuthConfigurationProperties {

    private String mysqlProductTag;
    private String mysqlModuleTag;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlTable;
    private String redisProductTag;
    private String redisModuleTag;
    private String redisHost;
    private int redisPort;
    private int redisIndex;
    private String redisPassword;
    private String accessKey;
    private String secretKey;
    private String containsAccessKey;
    private String tokenUrl;
    private String permitUrlStr;
    private String sessionUserTag;
    private String userConnection;
    private String userTable;
    private String usernameColumn;
    private String passwordColumn;

}
