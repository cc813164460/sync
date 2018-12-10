package com.hsgene.elasticsearch.sync.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @description:
 * @projectName: sync-elasticsearch
 * @package: com.hsgene.elasticsearch.sync.client
 * @author: maodi
 * @createDate: 2018/12/6 11:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class MysqlClient {

    private final static Logger LOGGER = LogManager.getLogger(MysqlClient.class);

    public static int maxWait = 1800000;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * 连接
     */
    private String mysqlUrl;

    public MysqlClient(String user, String password, String mysqlUrl) {
        this.user = user;
        this.password = password;
        this.mysqlUrl = "jdbc:mysql://" + mysqlUrl;
    }

    public DruidDataSource getDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            //com.mysql.jdbc.Driver
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            mysqlUrl += "?autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull&useSSL=true";
            dataSource.setUrl(mysqlUrl);
            dataSource.setInitialSize(StringUtil.POOL_NUM);
            dataSource.setMinIdle(StringUtil.POOL_NUM);
            dataSource.setMaxActive(StringUtil.POOL_NUM + 3);
            //启用监控统计功能,stat,log4j,wall
            dataSource.setFilters("stat");
            //游标是否开启，mysql不建议开启，mysql不支持游标
            dataSource.setPoolPreparedStatements(false);
            dataSource.setKeepAlive(false);
            dataSource.setMaxWait(maxWait);
            dataSource.setKillWhenSocketReadTimeout(false);
            dataSource.setResetStatEnable(true);
            dataSource.setEnable(true);
            dataSource.setAsyncInit(true);
            //有两个含义：1) Destroy线程会检测连接的间隔时间；2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明；
            dataSource.setTimeBetweenEvictionRunsMillis(300000);
            //连接保持空闲而不被驱逐的最长时间
            dataSource.setMinEvictableIdleTimeMillis(300000);
            //通过datasource.getConnontion() 取得的连接必须在removeAbandonedTimeout这么多秒内调用close()，要不然druid就会帮关闭连接
            dataSource.setRemoveAbandoned(true);
            dataSource.setRemoveAbandonedTimeout(maxWait);
            dataSource.setQueryTimeout(maxWait);
            dataSource.setTransactionQueryTimeout(maxWait);
        } catch (Exception e) {
            LOGGER.error("mysql连接出错", e);
            e.printStackTrace();
        }
        return dataSource;
    }

}
