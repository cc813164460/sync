package com.hsgene.hdas.api.auth.common;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @description: mysql操作类
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/26 16:11
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Data
public class MysqlUtil implements IDatabaseUtil {

    private final static int POOL_NUM = Runtime.getRuntime().availableProcessors() * 2 + 1;
    public static int maxActive = POOL_NUM + 5;

    private String productTag;
    private String moduleTag;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private String table;
    private String accessKey;
    private String secretKey;
    private boolean isPrint = false;

    private static DruidDataSource dataSource = null;

    public MysqlUtil() {
    }

    public MysqlUtil(String productTag, String moduleTag, String host, int port, String database, String user, String
            password, String table, boolean isPrint) {
        this.productTag = productTag;
        this.moduleTag = moduleTag;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.table = table;
        this.isPrint = isPrint;
    }

    public MysqlUtil(String productTag, String moduleTag, String host, int port, String database, String user, String
            password, String table, String accessKey, String secretKey) {
        this.productTag = productTag;
        this.moduleTag = moduleTag;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        this.table = table;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public DruidDataSource getDruidDataSource() throws Exception {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                dataSource = new DruidDataSource();
                dataSource.setDriverClassName(Constant.MYSQLDRIVER);
                dataSource.setUsername(user);
                dataSource.setPassword(password);
                String url = Constant.MYSQLURLHEAD + host + ":" + port + "/" + database + Constant.MYSQLURLFOOT;
                dataSource.setUrl(url);
                dataSource.setInitialSize(POOL_NUM);
                dataSource.setMinIdle(POOL_NUM);
                dataSource.setMaxActive(maxActive);
                //启用监控统计功能,stat,log4j,wall
                dataSource.setFilters("stat");
                dataSource.setPoolPreparedStatements(false);
                dataSource.setKeepAlive(false);
                dataSource.setMaxWait(1800000);
                dataSource.setKillWhenSocketReadTimeout(false);
                dataSource.setResetStatEnable(true);
                dataSource.setEnable(true);
                dataSource.setAsyncInit(true);
                //有两个含义：1) Destroy线程会检测连接的间隔时间；2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明；
                dataSource.setTimeBetweenEvictionRunsMillis(300000);
                //连接保持空闲而不被驱逐的最长时间
                dataSource.setMinEvictableIdleTimeMillis(300000);
                //通过datasource.getConnection() 取得的连接必须在removeAbandonedTimeout这么多秒内调用close()，要不然druid就会帮关闭连接
                dataSource.setRemoveAbandoned(true);
                dataSource.setRemoveAbandonedTimeout(1800000);
                dataSource.setQueryTimeout(1800000);
                dataSource.setTransactionQueryTimeout(1800000);
            }
            return dataSource;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param
     * @return com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey
     * @description 创建accessKey和secretKey，如果存在就直接获取，没有就创建新的
     * @author maodi
     * @createDate 2018/9/26 17:46
     */
    @Override
    public AccessKeyAndSecretKey createKeys() throws Exception {
        String accessKey = productTag + Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + UUID.randomUUID()
                .toString();
        String sql = "SELECT secret_key FROM `" + table + "` WHERE access_key = \'" + accessKey + "\'";
        try {
            DruidDataSource dataSource = getDruidDataSource();
            Connection con = dataSource.getConnection();
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            String secretKey = productTag + Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + UUID.randomUUID()
                    .toString();
            if (rs.next()) {
                secretKey = rs.getString(1);
            } else {
                sql = "INSERT INTO `" + table + "` (access_key, secret_key) VALUES(?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, accessKey);
                ps.setString(2, secretKey);
                ps.execute();
                ps.close();
            }
            rs.close();
            con.close();
            if (isPrint) {
                Constant.printInfo(accessKey, secretKey);
            }
            return new AccessKeyAndSecretKey(accessKey, secretKey);
        } catch (SQLException e) {
            throw new SQLException(sql, e);
        }
    }

    /**
     * @param accessKey
     * @return com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey
     * @description 根据accessKey获取secretKey
     * @author maodi
     * @createDate 2018/9/26 17:48
     */
    @Override
    public AccessKeyAndSecretKey get(String accessKey) throws Exception {
        String sql = "SELECT secret_key FROM `" + table + "` WHERE access_key = \'" + accessKey + "\'";
        try {
            DruidDataSource dataSource = getDruidDataSource();
            Connection con = dataSource.getConnection();
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            String secretKey = null;
            if (rs.next()) {
                secretKey = rs.getString(1);
            }
            rs.close();
            con.close();
            if (StringUtils.isBlank(secretKey)) {
                if (isPrint) {
                    Constant.printInfo(accessKey, secretKey);
                }
                return new AccessKeyAndSecretKey(accessKey, secretKey);
            }
            throw new IllegalArgumentException("This accessKey has no secretKey.");
        } catch (SQLException e) {
            throw new SQLException(sql, e);
        }
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 获取所有accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:49
     */
    @Override
    public Set<AccessKeyAndSecretKey> getAllKeys() throws Exception {
        String sql = "SELECT access_key,secret_key FROM `" + table + "`";
        return getKeysBySQL(sql);
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据项目标识和模块标识获取accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:49
     */
    @Override
    public Set<AccessKeyAndSecretKey> getKeysByProductTagAndModuleTag(String productTag, String moduleTag) throws
            Exception {
        String sql = "SELECT access_key,secret_key FROM `" + table + "` WHERE access_key like \'%" + productTag +
                     Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + "%\'";
        return getKeysBySQL(sql);
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据项目标识获取accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:50
     */
    @Override
    public Set<AccessKeyAndSecretKey> getKeysByProductTag(String productTag) throws Exception {
        String sql = "SELECT access_key,secret_key FROM `" + table + "` WHERE access_key like \'%" + productTag +
                     Constant.SEPARATOR + "%\'";
        return getKeysBySQL(sql);
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据模块标识获取accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:50
     */
    @Override
    public Set<AccessKeyAndSecretKey> getKeysByModuleTag(String moduleTag) throws Exception {
        String sql = "SELECT access_key,secret_key FROM `" + table + "` WHERE access_key like \'%" + moduleTag +
                     Constant.SEPARATOR + "%\'";
        return getKeysBySQL(sql);
    }

    /**
     * @param sql
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据sql获取set集合
     * @author maodi
     * @createDate 2018/9/27 15:17
     */
    private Set<AccessKeyAndSecretKey> getKeysBySQL(String sql) throws Exception {
        try {
            DruidDataSource dataSource = getDruidDataSource();
            Connection con = dataSource.getConnection();
            ResultSet rs = con.prepareStatement(sql).executeQuery();
            Set<AccessKeyAndSecretKey> accessKeyAndSecretKeySet = new HashSet<>(16);
            boolean hasKey = false;
            while (rs.next()) {
                String accessKey = rs.getString(1);
                String secretKey = rs.getString(2);
                if (isPrint) {
                    Constant.printInfo(accessKey, secretKey);
                }
                accessKeyAndSecretKeySet.add(new AccessKeyAndSecretKey(accessKey, secretKey));
                hasKey = true;
            }
            rs.close();
            con.close();
            if (!hasKey) {
                throw new IllegalArgumentException("does't exist any accessKey and secretKey.");
            }
            return accessKeyAndSecretKeySet;
        } catch (SQLException e) {
            throw new SQLException(sql, e);
        }
    }

}
