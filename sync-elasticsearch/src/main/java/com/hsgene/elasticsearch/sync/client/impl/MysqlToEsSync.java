package com.hsgene.elasticsearch.sync.client.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.hsgene.elasticsearch.sync.client.SyncData;
import com.hsgene.elasticsearch.sync.domain.PoolNumAndEverySize;
import com.hsgene.elasticsearch.sync.util.ElasticSearchPool;
import com.hsgene.elasticsearch.sync.util.MysqlClient;
import com.hsgene.elasticsearch.sync.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @description: mysql同步数据到elasticsearch
 * @projectName: sync-elasticsearch
 * @package: com.hsgene.elasticsearch.sync.client
 * @author: maodi
 * @createDate: 2018/12/6 11:46
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class MysqlToEsSync implements SyncData {
    private final static Logger LOGGER = LogManager.getLogger(MysqlToEsSync.class);

    /**
     * mysql地址配置名
     */
    private final static String MYSQL_HOST = "mysql.host";

    /**
     * mysql端口配置名
     */
    private final static String MYSQL_PORT = "mysql.port";

    /**
     * mysql数据库配置名
     */
    private final static String MYSQL_DATABASE = "mysql.database";

    /**
     * mysql用户配置名
     */
    private final static String MYSQL_USER = "mysql.user";

    /**
     * mysql密码配置名
     */
    private final static String MYSQL_PASSWORD = "mysql.password";

    /**
     * mysql查询的主表名
     */
    private final static String MYSQL_TABLE = "genetic_testing_package";

    /**
     * 连接地址
     */
    private static String host;

    /**
     * 端口
     */
    private static int port;

    /**
     * 数据库
     */
    private static String database;

    /**
     * 用户名
     */
    private static String user;

    /**
     * 密码
     */
    private static String password;

    /**
     * sql末尾的条件
     */
    public static String sqlSuf = " where delete_flag = 0";

    /**
     * elasticsearch目标集群节点集配置名
     */
    private final static String TARGET_CLUSTER_NODES = "elasticsearch.target.cluster-nodes";

    /**
     * elasticsearch目标集群名称配置名
     */
    private final static String TARGET_CLUSTER_NAME = "elasticsearch.target.cluster-name";

    /**
     * elasticsearch目标索引配置名
     */
    private final static String TARGET_INDEX = "elasticsearch.target.index";

    /**
     * elasticsearch目标类型名称配置名
     */
    private final static String TARGET_TYPE_NAME = "elasticsearch.target.type-name";

    /**
     * 目标elasticsearch名称
     */
    private static String clusterName;

    /**
     * 目标elasticsearch索引名称
     */
    private static String index;

    /**
     * 目标elasticsearch类型名称
     */
    private static String typeName;

    /**
     * 目标集群节点
     */
    private static List<String> clusterNodes;

    public MysqlToEsSync(Properties prop) {
        try {
            host = prop.getProperty(MYSQL_HOST);
            port = Integer.valueOf(prop.getProperty(MYSQL_PORT));
            database = prop.getProperty(MYSQL_DATABASE);
            user = prop.getProperty(MYSQL_USER);
            password = prop.getProperty(MYSQL_PASSWORD);
        } catch (Exception e) {
            LOGGER.error("获取mysql配置参数时出错", e);
        }
        try {
            clusterNodes = Arrays.asList(prop.getProperty(TARGET_CLUSTER_NODES).split(","));
            clusterName = prop.getProperty(TARGET_CLUSTER_NAME);
            index = prop.getProperty(TARGET_INDEX);
            typeName = prop.getProperty(TARGET_TYPE_NAME);
        } catch (Exception e) {
            LOGGER.error("获取elasticsearch配置参数时出错", e);
        }
    }

    /**
     * @param
     * @return void
     * @description 校验配置参数的合法性
     * @author maodi
     * @createDate 2018/12/6 16:32
     */
    private void checkMysqlParameter() {
        if (!StringUtil.ipCheck(host)) {
            throw new IllegalArgumentException("mysql.host地址不合法，host地址为[" + host + "]");
        }
        if (!StringUtil.portCheck(port)) {
            throw new IllegalArgumentException("mysql.port端口不合法，port端口为[" + port + "]");
        }
        for (String clusterNode : clusterNodes) {
            String[] hostAndPort;
            try {
                hostAndPort = clusterNode.split(":");
            } catch (Exception e) {
                throw new IllegalArgumentException("elasticsearch.target.cluster-nodes配置有误");
            }
            int port;
            try {
                port = Integer.valueOf(hostAndPort[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("elasticsearch.target.cluster-nodes配置中端口不是数字");
            }
            if (!StringUtil.ipCheck(hostAndPort[0])) {
                throw new IllegalArgumentException("elasticsearch.target.cluster-nodes配置中host地址不合法，host地址为[" + host +
                                                   "]");
            }
            if (!StringUtil.portCheck(port)) {
                throw new IllegalArgumentException("elasticsearch.target.cluster-nodes配置中port地址不合法，port端口为[" + port +
                                                   "]");
            }
        }
    }

    @Override
    public void syncData() {
        try {
            checkMysqlParameter();
            LOGGER.info("开始从" + host + "的mysql的" + database + "数据库" + database + "同步数据到" + clusterName + "的" + index
                        + "索引");
            String url = host + ":" + port + "/" + database;
            LOGGER.info("开始初始化mysql连接池...");
            MysqlClient client = new MysqlClient(user, password, url);
            DruidDataSource mysqlDds = client.getDruidDataSource();
            LOGGER.info("初始化mysql连接池完成");
            Connection con = mysqlDds.getConnection();
            LOGGER.info("开始初始化elasticsearch连接池...");
            ElasticSearchPool esPool = new ElasticSearchPool(clusterName, clusterNodes);
            LOGGER.info("初始化elasticsearch连接池完成");
            String sql = "select count(*) from " + MYSQL_TABLE + sqlSuf;
            long startTime = System.currentTimeMillis();
            ResultSet result = con.prepareStatement(sql).executeQuery();
            result.next();
            int totalCount = result.getInt(1);
            LOGGER.info("mysql数据总量为：" + totalCount);
            PoolNumAndEverySize poolNumAndEverySize = StringUtil.calcPoolNumAndEverySize(totalCount);
            int everySize = poolNumAndEverySize.getEverySize();
            int poolNum = poolNumAndEverySize.getPoolNum();
            List<Future<Integer>> futures = new ArrayList<>();
            ThreadPoolExecutor pool = new ThreadPoolExecutor(StringUtil.POOL_NUM, StringUtil.POOL_NUM, 0,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(poolNum));
            for (int i = 0; i < poolNum; i++) {
                int from = i * everySize;
                MysqlToEsSyncEverySend everySend = new MysqlToEsSyncEverySend(index, typeName, esPool, mysqlDds,
                        from, everySize);
                Future<Integer> future = pool.submit(everySend);
                futures.add(future);
                everySend = null;
                future = null;
            }
            //停止线程池，不能再加入新的线程
            pool.shutdown();
            //等待线程执行完
            int syncTotalCount = StringUtil.getAllCount(futures);
            LOGGER.info("正在关闭mysql连接池...");
            mysqlDds.close();
            LOGGER.info("mysql连接池关闭成功");
            LOGGER.info("正在关闭elasticsearch连接池...");
            esPool.destroy();
            LOGGER.info("elasticsearch连接池关闭成功");
            printSpeed(startTime, syncTotalCount);
        } catch (Exception e) {
            LOGGER.error("同步mysql数据到elasticsearch时出错", e);
        }
    }

    /**
     * @param startTime  开始时间
     * @param totalCount 同步数量
     * @return void
     * @description 输出同步结果
     * @author maodi
     * @createDate 2018/12/6 14:43
     */
    public static void printSpeed(long startTime, long totalCount) {
        float spendTime = (float) (System.currentTimeMillis() - startTime) / 1000;
        float speed = totalCount / spendTime;
        String info = "\n******同步完成******\n数量：" + totalCount + ";\n" + "时间：" + spendTime + "秒;\n每秒：" + String.format
                ("%.2f", speed) + "条;\n";
        LOGGER.info(info);
    }
}

class MysqlToEsSyncEverySend implements Callable {

    /**
     * mysql查询数据的sql
     */
    private final static String MYSQL_SQL = "select gtp.id,gtp.package_name as name,gtpr.drug_id as medicineId,gtpr" +
                                            ".drug_name as medicineName,gtpr.targets,gtpr.detection,gtpr.clinical," +
                                            "gtpr.detection_price as price,gtpr.commission,gtpr.cancer_id as " +
                                            "cancerId,gtpr.cancer_name as cancerName,gtpr.nature,gtpr.description,gtp" +
                                            ".genetic_testing_agency_name as orgs,gtpa.shopping_cart_amount as " +
                                            "usageCount,gtpr.serial_number as number,gtpd.detection_period as " +
                                            "testPeriod,gtpr.commission as integration,gtpr.policy_type as " +
                                            "policyType,gtp.is_used as isUsed from genetic_testing_package gtp left " +
                                            "join genetic_testing_package_addtion gtpa on gtp.id = gtpa.package_id " +
                                            "left join genetic_testing_product gtpr on gtp.id = gtpr.package_id left " +
                                            "join genetic_testing_product_detail gtpd on gtpr.id = gtpd.product_id " +
                                            "where gtp.delete_flag = 0 group by gtp.id";

    private final static Logger LOGGER = LogManager.getLogger(MysqlToEsSyncEverySend.class);

    private ElasticSearchPool esPool;
    private DruidDataSource mysqlDds;
    private String index;
    private String typeName;
    private int from;
    private int everySize;

    public MysqlToEsSyncEverySend(String index, String typeName, ElasticSearchPool esPool, DruidDataSource mysqlDds,
                                  int from, int everySize) {
        this.index = index;
        this.typeName = typeName;
        this.esPool = esPool;
        this.mysqlDds = mysqlDds;
        this.from = from;
        this.everySize = everySize;
    }

    @Override
    public Object call() {
        int offset = from * everySize;
        int start = from + 1;
        int count = 0;
        try {
            TransportClient esClient = esPool.getResource();
            IndexRequestBuilder builder = esClient.prepareIndex(index, typeName);
            Connection con = mysqlDds.getConnection();
            String sql = MYSQL_SQL + " limit " + everySize + " offset " + offset;
            ResultSet resultSet = con.prepareStatement(sql).executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String json = StringUtil.mysqlDataToJson(resultSet);
                IndexResponse indexResponse = builder.setId(id).setSource(json, XContentType.JSON).execute()
                        .actionGet();
                RestStatus restStatus = indexResponse.status();
                int status = restStatus.getStatus();
                if (status != RestStatus.CREATED.getStatus() && status != RestStatus.OK.getStatus()) {
                    LOGGER.warn("同步数据有误，同步操作状态为" + restStatus.name() + "，有误数据id为" + id + "，同步程序继续运行");
                }
                id = null;
                json = null;
                indexResponse = null;
                restStatus = null;
                count++;
            }
            resultSet.close();
            con.close();
            builder = null;
            con = null;
            sql = null;
            resultSet = null;
            int end = from + count;
            //归还elasticsearch连接
            esPool.returnResource(esClient);
            LOGGER.info("******同步完第" + start + "至" + end + "条数据******");
        } catch (Exception e) {
            LOGGER.error("同步第" + start + "条到第" + (from + everySize) + "条数据时出错", e);
        }
        return count;
    }

}
