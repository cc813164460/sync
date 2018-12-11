package com.hsgene.mysql.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.hbase.model.HbaseInfo;
import com.hsgene.hbase.model.TableInfo;
import com.hsgene.model.AddDataConfig;
import com.hsgene.model.DatabaseInfo;
import com.hsgene.model.TargetInfo;
import com.hsgene.mysql.model.MysqlAddDataConfig;
import com.hsgene.mysql.model.MysqlInfo;
import com.hsgene.util.AbstractWriteData;
import com.hsgene.util.StringUtil;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 12:30 2017/12/12
 * @Modified By:
 */
public class WriteMysqlDataToHbase extends WriteMysqlData {

    private final static Logger LOGGER = Logger.getLogger(WriteMysqlDataToHbase.class);
    private DruidDataSource hbaseDds;

    private void initHbaseConnection(HbaseInfo hbaseInfo) {
        String url = ConstantSymbol.PHOENIX_JDBC + hbaseInfo.getUrl();
        hbaseDds = getDruidDataSource(ConstantSymbol.PHOENIX_DRIVERCLASS, url, initialSize, initialSize, maxActive,
            "stat", false);
    }

    @Override
    public void writeAllData(DatabaseInfo databaseInfo, TargetInfo targetInfo) {
        try {
            LOGGER.info("msyql全量导入开始！");
            HbaseInfo hbaseInfo = (HbaseInfo) targetInfo.getObject();
            initHbaseConnection(hbaseInfo);
            MysqlInfo mysqlInfo = (MysqlInfo) databaseInfo;
            String snapshotMysqlUrl = mysqlInfo.getSnapshotMysqlUrl();
            String user = snapshotMysqlUrl.substring(snapshotMysqlUrl.indexOf("user=") + "user=".length(),
                snapshotMysqlUrl.indexOf("&password"));
            String password = snapshotMysqlUrl.substring(snapshotMysqlUrl.indexOf("&password=") + "&password=".length
                (), snapshotMysqlUrl.length());
            String mysqlUrl = ConstantSymbol.MYSQL_JDBC + snapshotMysqlUrl.substring(0, snapshotMysqlUrl.indexOf("?"));
            mysqlInfo.setUser(user);
            mysqlInfo.setPassword(password);
            //mysqlInfo使用快照的，mysqlUrl也是用快照的
            initConnection(mysqlInfo, mysqlUrl);
            List<TableInfo> tableInfoList = hbaseInfo.getTableInfoList();
            int length = tableInfoList.size();
            ThreadPoolExecutor pool = new ThreadPoolExecutor(POOL_NUM, maxActive, keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(length));
            for (int i = 0; i < length; i++) {
                TableInfo tableInfo = tableInfoList.get(i);
                String hbaseMainColumn = tableInfo.getHbaseColumnJSONArray().getString(0);
                EveryMysqlSend everyMysqlSend = new EveryMysqlSend(tableInfo, writeMysqlDds, hbaseDds, mysqlInfo,
                    hbaseMainColumn);
                pool.execute(everyMysqlSend);
            }
            //阻塞等待其他线程执行完
            pool.shutdown();
            while (!pool.isTerminated()) {
            }
            LOGGER.info("mysql全量导入完成！");
        } catch (Exception e) {
            LOGGER.error("mysql全量导入出错", e);
        }
    }

    @Override
    public void writeAddData(DatabaseInfo databaseInfo, TargetInfo targetInfo, AddDataConfig addDataConfig) {
        LOGGER.info("mysql增量同步开始！");
        MysqlAddDataConfig config = (MysqlAddDataConfig) addDataConfig;
        MysqlInfo mysqlInfo = (MysqlInfo) databaseInfo;
        String destination = config.getCanalDestination();
        HbaseInfo hbaseInfo = (HbaseInfo) targetInfo.getObject();
        initHbaseConnection(hbaseInfo);
        initConnection(mysqlInfo);
        if (destination != null) {
            CanalConnector connector = StringUtil.getConnector(config, hbaseInfo, mysqlInfo);
            try {
                List<TableInfo> tableInfoList = hbaseInfo.getTableInfoList();
                List<String> allTableName = new ArrayList<>();
                for (TableInfo tableInfo : tableInfoList) {
                    allTableName.add(tableInfo.getTable());
                }
                while (true) {
                    try {
                        Thread.sleep(sleep);
                    } catch (Exception e) {
                        LOGGER.error("", e);
                    }
                    // 获取指定数量的数据
                    Message message = null;
                    long batchId = -2;
                    int getWithoutAckCount = 0;
                    while (getWithoutAckCount < ConstantSymbol.RETRY_COUNT) {
                        try {
                            message = connector.getWithoutAck(batchSize);
                            batchId = message.getId();
                            if (getWithoutAckCount > 0) {
                                LOGGER.info("mysql getWithoutAck获取connector第" + getWithoutAckCount + "次重试成功！");
                                getWithoutAckCount = 0;
                            }
                            break;
                        } catch (CanalClientException e) {
                            connector.disconnect();
                            Thread.sleep(ConstantSymbol.RETRY_SLEEP);
                            getWithoutAckCount++;
                            LOGGER.warn("mysql getWithoutAck获取connector第" + getWithoutAckCount + "次重试", e);
                            connector.connect();
                            if (getWithoutAckCount >= ConstantSymbol.RETRY_COUNT) {
                                LOGGER.error("mysql getWithoutAck获取connector超过20次", e);
                            }
                        }
                    }
                    List<CanalEntry.Entry> entryList = message.getEntries();
                    int size = entryList.size();
                    if (batchId == -2) {
                        continue;
                    }
                    if (batchId != -1 && size != 0) {
                        try {
                            StringUtil.mysqlAddAction(entryList, tableInfoList, hbaseDds, writeMysqlDds, allTableName,
                                destination);
                        } catch (CommunicationsException e) {
                            initConnection(mysqlInfo);
                            LOGGER.warn("mysql重新获取druid");
                        }
                    }
                    try {
                        connector.ack(batchId);
                    } catch (CanalClientException e) {
                        connector.disconnect();
                        Thread.sleep(ConstantSymbol.RETRY_SLEEP);
                        connector.connect();
                        LOGGER.warn("mysql ack batchId " + batchId + " 时出错，重新获取connector");
                    }
                    message = null;
                    entryList = null;
                    System.gc();
                }
            } catch (Exception e) {
                LOGGER.error("mysql增量出错", e);
            }
        }
    }

}

class MysqlSendToHbase implements Runnable {
    private Connection hbaseConnection;
    private Connection mysqlConnection;
    private long everyCount;
    private long k;
    private String table;
    private String sql;
    private final static Logger LOGGER = Logger.getLogger(MysqlSendToHbase.class);

    MysqlSendToHbase(Connection hbaseConnection, Connection mysqlConnection, long everyCount, long k, String table,
                     String sql) {
        this.hbaseConnection = hbaseConnection;
        this.mysqlConnection = mysqlConnection;
        this.everyCount = everyCount;
        this.k = k;
        this.table = table;
        this.sql = sql;
    }

    @Override
    public void run() {
        try {
            long skip = k * everyCount;
            String offset = " offset ";
            String limit = " limit ";
            if (sql.indexOf(offset) != -1) {
                int offsetSql = Integer.valueOf(sql.substring(sql.indexOf(offset) + offset.length()));
                skip += offsetSql;
            }
            String subSql = sql;
            if (sql.indexOf(limit) != -1) {
                subSql = sql.substring(0, sql.indexOf(limit));
            }
            String selectSQL = subSql + " LIMIT " + everyCount + " OFFSET " + skip;
            StringUtil.resultSetUpsertInto(mysqlConnection, hbaseConnection, selectSQL, table);
            hbaseConnection.close();
            mysqlConnection.close();
            LOGGER.info(table + ConstantSymbol.SPLIT + "发送完：" + skip + ConstantSymbol.SPLIT + everyCount);
            selectSQL = null;
            System.gc();
        } catch (Exception e) {
            LOGGER.error(sql, e);
        }
    }
}

class EveryMysqlSend implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(EveryMysqlSend.class);
    private TableInfo tableInfo;
    private int POOL_NUM = AbstractWriteData.POOL_NUM;
    private int maxActive = AbstractWriteData.maxActive;
    private DruidDataSource writeMysqlDds;
    private DruidDataSource hbaseDds;
    private MysqlInfo mysqlInfo;
    private int EVERY_COUNT_MAX = AbstractWriteData.EVERY_COUNT_MAX;
    private long keepAliveTime = AbstractWriteData.keepAliveTime;
    private String hbaseMainColumn;

    public EveryMysqlSend(TableInfo tableInfo, DruidDataSource writeMysqlDds, DruidDataSource hbaseDds, MysqlInfo
        mysqlInfo, String hbaseMainColumn) {
        this.tableInfo = tableInfo;
        this.writeMysqlDds = writeMysqlDds;
        this.hbaseDds = hbaseDds;
        this.mysqlInfo = mysqlInfo;
        this.hbaseMainColumn = hbaseMainColumn;
    }

    @Override
    public void run() {
        String hbaseTable = tableInfo.getTable();
        try {
            if (tableInfo.getHbaseColumnJSONArray() != null) {
                JSONArray hbaseColumnJSONArray = tableInfo.getHbaseColumnJSONArray();
                String pk = hbaseColumnJSONArray.getString(0);
                String columnFamily = tableInfo.getColumnFamily();
                String columnInfo = StringUtil.getCreateColumnInfo(hbaseColumnJSONArray, pk, columnFamily);
                //处理是否是全数据库或者全连接
                long start = System.currentTimeMillis();
                int count = 0;
                String sql = tableInfo.getSql();
                Connection connection = writeMysqlDds.getConnection();
                //表名为空时传输全部表，不为空则传输指定表
                String getDatabase = mysqlInfo.getDatabase();
                String countSQL = "select count(*) from (" + sql + ") temp";
                ResultSet result = connection.prepareStatement(countSQL).executeQuery();
                while (result.next()) {
                    count = result.getInt(1);
                    break;
                }
                result.close();
                Connection hbaseConnection = hbaseDds.getConnection();
                StringBuilder databaseAndTable = new StringBuilder(getDatabase);
                databaseAndTable.append(".");
                databaseAndTable.append(hbaseTable);
                //创建表指定主键
                String createSQL = "CREATE TABLE IF NOT EXISTS \"" + hbaseTable + "\"(" + columnInfo + ")";
                try {
                    hbaseConnection.prepareStatement(createSQL).executeUpdate();
                } catch (SQLException e) {
                    LOGGER.error("发送" + hbaseTable + "表的mysql数据时出错\n" + createSQL, e);
                }
                hbaseConnection.commit();
                hbaseConnection.close();
                int everyCount = count / (POOL_NUM - 1);
                int poolNum;
                if (everyCount > EVERY_COUNT_MAX) {
                    everyCount = EVERY_COUNT_MAX;
                    poolNum = (count / everyCount) + 1;
                } else {
                    everyCount = count;
                    poolNum = 1;
                }
                ThreadPoolExecutor pool = new ThreadPoolExecutor(POOL_NUM, maxActive, keepAliveTime, TimeUnit
                    .MILLISECONDS, new LinkedBlockingDeque<Runnable>(poolNum));
                for (int k = 0; k < poolNum; k++) {
                    MysqlSendToHbase mysqlSendToHbase = new MysqlSendToHbase(hbaseDds.getConnection(), writeMysqlDds
                        .getConnection(), everyCount, k, hbaseTable, sql);
                    pool.execute(mysqlSendToHbase);
                }
                pool.shutdown();
                //等待线程执行完
                while (!pool.isTerminated()) {
                }
                StringUtil.printSpeed(start, count, hbaseTable, LOGGER);
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error("发送" + hbaseTable + "表的mysql数据时出错", e);
        }
    }
}