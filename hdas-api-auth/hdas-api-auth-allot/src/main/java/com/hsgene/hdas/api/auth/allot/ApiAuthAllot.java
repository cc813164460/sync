package com.hsgene.hdas.api.auth.allot;

import com.hsgene.hdas.api.auth.common.MysqlUtil;
import com.hsgene.hdas.api.auth.common.RedisUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @description: api鉴权使用工具类
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/18 10:31
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ApiAuthAllot {

    private final static String MYSQL_PRODUCT_TAG = "mysql.productTag";
    private final static String MYSQL_MODULE_TAG = "mysql.moduleTag";
    private final static String MYSQL_HOST = "mysql.host";
    private final static String MYSQL_PORT = "mysql.port";
    private final static String MYSQL_DATABASE = "mysql.database";
    private final static String MYSQL_USER = "mysql.user";
    private final static String MYSQL_PASSWORD = "mysql.password";
    private final static String MYSQL_TABLE = "mysql.table";
    private final static String REDIS_PRODUCT_TAG = "redis.productTag";
    private final static String REDIS_MODULE_TAG = "redis.moduleTag";
    private final static String REDIS_HOST = "redis.host";
    private final static String REDIS_PORT = "redis.port";
    private final static String REDIS_INDEX = "redis.index";
    private final static String REDIS_PASSWORD = "redis.password";

    private static String mysqlProductTag;
    private static String mysqlModuleTag;
    private static String mysqlHost;
    private static int mysqlPort;
    private static String mysqlDatabase;
    private static String mysqlUser;
    private static String mysqlPassword;
    private static String mysqlTable;
    private static String redisProductTag;
    private static String redisModuleTag;
    private static String redisHost;
    private static int redisPort;
    private static int redisIndex;
    private static String redisPassword;

    static {
        Properties properties = new Properties();
        InputStream in = ApiAuthAllot.class.getClassLoader().getResourceAsStream("parameter.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mysqlProductTag = properties.getProperty(MYSQL_PRODUCT_TAG);
        mysqlModuleTag = properties.getProperty(MYSQL_MODULE_TAG);
        mysqlHost = properties.getProperty(MYSQL_HOST);
        mysqlPort = Integer.valueOf(properties.getProperty(MYSQL_PORT));
        mysqlDatabase = properties.getProperty(MYSQL_DATABASE);
        mysqlUser = properties.getProperty(MYSQL_USER);
        mysqlPassword = properties.getProperty(MYSQL_PASSWORD);
        mysqlTable = properties.getProperty(MYSQL_TABLE);
        redisProductTag = properties.getProperty(REDIS_PRODUCT_TAG);
        redisModuleTag = properties.getProperty(REDIS_MODULE_TAG);
        redisHost = properties.getProperty(REDIS_HOST);
        redisPort = Integer.valueOf(properties.getProperty(REDIS_PORT));
        redisIndex = Integer.valueOf(properties.getProperty(REDIS_INDEX));
        redisPassword = properties.getProperty(REDIS_PASSWORD);
    }

    public static void printInfo() {
        String info = "参数说明:java -jar hdas-api-auth-allot.jar 04 [productTag] [moduleTag]\n" +
                      "    00:在mysql中创建accessKey和secretKey\n" +
                      "    01:查找mysql中所有accessKey和secretKey\n" +
                      "    02 [productTag]:根据productTag查找mysql中所有accessKey和secretKey\n" +
                      "    03 [moduleTag]:根据moduleTag查找mysql中所有accessKey和secretKey\n" +
                      "    04 [productTag] [moduleTag]:根据productTag和moduleTag查找mysql中所有accessKey和secretKey\n" +
                      "    10:在redis中创建accessKey和secretKey\n" +
                      "    11:查找redis中所有accessKey和secretKey\n" +
                      "    12 [productTag]:根据productTag查找redis中所有accessKey和secretKey\n" +
                      "    13 [moduleTag]:根据moduleTag查找redis中所有accessKey和secretKey\n" +
                      "    14 [productTag] [moduleTag]:根据productTag和moduleTag查找redis中所有accessKey和secretKey";
        System.out.println(info);
    }

    public static void main(String[] args) {
        try {
            MysqlUtil mu = new MysqlUtil(mysqlProductTag, mysqlModuleTag, mysqlHost, mysqlPort, mysqlDatabase,
                    mysqlUser, mysqlPassword, mysqlTable, true);
            RedisUtil ru = new RedisUtil(redisProductTag, redisModuleTag, redisHost, redisPort, redisIndex,
                    redisPassword, true);
            if (args.length > 0) {
                int parameter = Integer.valueOf(args[0]);
                switch (parameter) {
                    case 00:
                        mu.createKeys();
                        break;
                    case 01:
                        mu.getAllKeys();
                        break;
                    case 02:
                        if (args.length < 2) {
                            printInfo();
                        } else {
                            mu.getKeysByProductTag(args[1]);
                        }
                        break;
                    case 03:
                        if (args.length < 2) {
                            printInfo();
                        } else {
                            mu.getKeysByModuleTag(args[1]);
                        }
                        break;
                    case 04:
                        if (args.length < 3) {
                            printInfo();
                        } else {
                            mu.getKeysByProductTagAndModuleTag(args[1], args[2]);
                        }
                        break;
                    case 10:
                        ru.createKeys();
                        break;
                    case 11:
                        ru.getAllKeys();
                        break;
                    case 12:
                        if (args.length < 2) {
                            printInfo();
                        } else {
                            ru.getKeysByProductTag(args[1]);
                        }
                        break;
                    case 13:
                        if (args.length < 2) {
                            printInfo();
                        } else {
                            ru.getKeysByModuleTag(args[1]);
                        }
                        break;
                    case 14:
                        if (args.length < 3) {
                            printInfo();
                        } else {
                            ru.getKeysByProductTagAndModuleTag(args[1], args[2]);
                        }
                        break;
                    default:
                        printInfo();
                        break;
                }
            } else {
                printInfo();
            }
        } catch (Exception e) {
            printInfo();
            e.printStackTrace();
        }
    }

}
