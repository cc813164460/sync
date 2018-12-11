package com.hsgene.hdas.api.auth.common;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/27 9:52
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class Constant {

    public final static String FALSE = "false";
    public final static String COMMA = ",";
    public final static String SEPARATOR = "-";
    public final static String ACCESSKEY = "accessKey";
    public final static String SECRETKEY = "secretKey";
    public final static String MYSQLDRIVER = "com.mysql.jdbc.Driver";
    public final static String MYSQLURLHEAD = "jdbc:mysql://";
    public final static String MYSQLURLFOOT =
            "?autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull&useSSL=true";

    public final static String regex = "^[-\\+]?[\\d]*$";

    public static void printInfo(String accessKey, String secretKey) {
        String print = Constant.ACCESSKEY + ":" + accessKey + ";" + Constant.SECRETKEY + ":" + secretKey;
        log.info(print);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

}
