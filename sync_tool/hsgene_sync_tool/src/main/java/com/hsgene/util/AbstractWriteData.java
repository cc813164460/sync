package com.hsgene.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.model.*;
import com.hsgene.mongodb.util.WriteMongodbData;
import com.hsgene.mysql.util.WriteMysqlData;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:11 2017/10/26
 * @Modified By:
 */
public abstract class AbstractWriteData implements WriteData {

    private final static Logger LOGGER = Logger.getLogger(AbstractWriteData.class);
    public final static int POOL_NUM = Runtime.getRuntime().availableProcessors() * 2 + 3;
    public static int initialSize = POOL_NUM;
    public static int maxActive = initialSize + 10;
    public static int maxWait = 1800000;
    public static int EVERY_COUNT_MAX = 570;
    public static long keepAliveTime = 0;
    public static int batchSize = 32;
    public static long sleep = 100;

    public DruidDataSource getDruidDataSource(String driverClassName, String url, int initialSize, int minIdle, int
        maxActive, String filters, boolean poolPreparedStatements) {
        return getDruidDataSource(driverClassName, null, null, url, initialSize, minIdle, maxActive,
            filters, poolPreparedStatements);
    }

    public DruidDataSource getDruidDataSource(String driverClassName, String user, String password, String url, int
        initialSize, int minIdle, int maxActive, String filters, boolean poolPreparedStatements) {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            //com.mysql.jdbc.Driver
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            //jdbc:mysql://127.0.0.1:3306/jspdemo
            if (driverClassName.indexOf("mysql") != -1) {
                url += "?autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull";
            }
            dataSource.setUrl(url);
            dataSource.setInitialSize(initialSize);
            dataSource.setMinIdle(minIdle);
            dataSource.setMaxActive(maxActive);
            //启用监控统计功能,stat,log4j,wall
            dataSource.setFilters(filters);
            dataSource.setPoolPreparedStatements(poolPreparedStatements);
            dataSource.setKeepAlive(false);
//            dataSource.setMaxWait(maxWait);
            dataSource.setKillWhenSocketReadTimeout(false);
            dataSource.setResetStatEnable(true);
            dataSource.setEnable(true);
            dataSource.setAsyncInit(true);
            if (driverClassName.indexOf("phoenix") != -1) {
                dataSource.setValidationQuery("select 1");
                //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
                dataSource.setTestWhileIdle(true);
            }
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
            LOGGER.error(driverClassName + "连接出错！", e);
            e.printStackTrace();
        }
        return dataSource;
    }

    @Override
    public void writeData(UrlInfos urlInfos) {
        List<UrlInfo> urlInfoList = urlInfos.getUrlInfoList();
        int count = urlInfoList.size();
        ExecutorService pool = new ThreadPoolExecutor(POOL_NUM, maxActive, keepAliveTime, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(count));
        for (UrlInfo urlInfo : urlInfoList) {
            EveryThread everyThread = new EveryThread(urlInfo);
            pool.execute(everyThread);
        }
        pool.shutdown();
        while (!pool.isTerminated()) {
        }
        LOGGER.info("同步完成！");
    }

}

class EveryThread extends Thread {

    private final static Logger LOGGER = Logger.getLogger(EveryThread.class);
    private UrlInfo urlInfo;

    public EveryThread(UrlInfo urlInfo) {
        this.urlInfo = urlInfo;
    }

    @Override
    public void run() {
        String syncType = urlInfo.getSyncType();
        DatabaseInfo databaseInfo = urlInfo.getDatabaseInfo();
        TargetInfo targetInfo = urlInfo.getTargetInfo();
        AddDataConfig addDataConfig = urlInfo.getAddDataConfig();
        String dataType = urlInfo.getDataType();
        try {
            if (ConstantSymbol.MONGODB.equals(dataType)) {
                WriteMongodbData writeMongodbData = new WriteMongodbData();
                if (ConstantSymbol.SYNC_ALL.equals(syncType)) {
                    writeMongodbData.writeAllData(databaseInfo, targetInfo);
                } else if (ConstantSymbol.SYNC_ALL_ADD.equals(syncType)) {
                    writeMongodbData.writeAllData(databaseInfo, targetInfo);
                    SaveFlag.setSyncTypeToAdd();
                    writeMongodbData.writeAddData(databaseInfo, targetInfo, addDataConfig);
                } else if (ConstantSymbol.SYNC_ADD.equals(syncType)) {
                    writeMongodbData.writeAddData(databaseInfo, targetInfo, addDataConfig);
                } else {
                    throw new IllegalArgumentException(syncType + "同步方式参数有误：");
                }
            } else if (ConstantSymbol.MYSQL.equals(dataType)) {
                WriteMysqlData writeMysqlData = new WriteMysqlData();
                if (ConstantSymbol.SYNC_ALL.equals(syncType)) {
                    writeMysqlData.writeAllData(databaseInfo, targetInfo);
                } else if (ConstantSymbol.SYNC_ALL_ADD.equals(syncType)) {
                    writeMysqlData.writeAllData(databaseInfo, targetInfo);
                    SaveFlag.setSyncTypeToAdd();
                    writeMysqlData.writeAddData(databaseInfo, targetInfo, addDataConfig);
                } else if (ConstantSymbol.SYNC_ADD.equals(syncType)) {
                    writeMysqlData.writeAddData(databaseInfo, targetInfo, addDataConfig);
                } else {
                    throw new IllegalArgumentException(syncType + "同步方式参数有误：");
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("参数错误", e);
            e.printStackTrace();
        }
        LOGGER.info("urlInfo导入完成");
    }
}
