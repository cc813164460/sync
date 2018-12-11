package com.hsgene.mongodb.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.hsgene.mongodb.model.MongodbAddDataConfig;
import com.hsgene.mongodb.model.MongodbInfo;
import com.hsgene.util.AbstractWriteData;
import com.hsgene.util.SaveFlag;
import com.hsgene.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCursorNotFoundException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:42 2017/12/12
 * @Modified By:
 */
public class WriteMongodbDataToHbase extends WriteMongodbData {

    private final static Logger LOGGER = Logger.getLogger(WriteMongodbDataToHbase.class);
    private DruidDataSource hbaseDds;
    private DruidDataSource mysqlDds;

    private void initHbaseConnection(HbaseInfo hbaseInfo, String mysqlUrl) {
        String url = ConstantSymbol.PHOENIX_JDBC + hbaseInfo.getUrl();
        String user = mysqlUrl.substring(mysqlUrl.indexOf("user=") + "user=".length(), mysqlUrl.indexOf("&password"));
        String password = mysqlUrl.substring(mysqlUrl.indexOf("&password=") + "&password=".length(), mysqlUrl.length());
        mysqlUrl = ConstantSymbol.MYSQL_JDBC + mysqlUrl.substring(0, mysqlUrl.indexOf("?"));
        hbaseDds = getDruidDataSource(ConstantSymbol.PHOENIX_DRIVERCLASS, url, initialSize, initialSize, maxActive,
            "stat", false);
        mysqlDds = getDruidDataSource(ConstantSymbol.MYSQL_DRIVERCLASS, user, password, mysqlUrl, initialSize,
            initialSize, maxActive, "stat", false);
    }

    @Override
    public void writeAllData(DatabaseInfo databaseInfo, TargetInfo targetInfo) {
        try {
            LOGGER.info("mongo全量导入开始！");
            HbaseInfo hbaseInfo = (HbaseInfo) targetInfo.getObject();
            MongodbInfo mongodbInfo = (MongodbInfo) databaseInfo;
            //mysql使用快照地址
            initHbaseConnection(hbaseInfo, mongodbInfo.getSnapshotMysqlUrl());
            initConnection(mongodbInfo);
            List<TableInfo> tableInfoList = hbaseInfo.getTableInfoList();
            int length = tableInfoList.size();
            String currentTime = String.valueOf(System.currentTimeMillis());
            ThreadPoolExecutor pool = new ThreadPoolExecutor(POOL_NUM, maxActive, keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(length));
            for (int i = 0; i < length; i++) {
                TableInfo tableInfo = tableInfoList.get(i);
                EveryMongoSend everyMongoSend = new EveryMongoSend(tableInfo, mongodbInfo, hbaseDds, mysqlDds,
                    mongoDatabase);
                pool.execute(everyMongoSend);
            }
            pool.shutdown();
            while (!pool.isTerminated()) {
            }
            int cl = currentTime.length();
            int seconds = Integer.valueOf(currentTime.substring(0, cl - 3));
            SaveFlag.replaceTs(new BsonTimestamp(seconds, 1));
            LOGGER.info("mongo全量导入完成！");
        } catch (Exception e) {
            LOGGER.error("全量导入时出错", e);
        }
    }

    @Override
    public void writeAddData(DatabaseInfo databaseInfo, TargetInfo targetInfo, AddDataConfig addDataConfig) {
        MongoCollection<Document> oplogCollection = null;
        MongoCollection<Document> mongoCollection;
        MongodbInfo mongodbInfo = (MongodbInfo) databaseInfo;
        try {
            LOGGER.info("mongo增量同步开始！");
            //获取mysql中变动信息
            HbaseInfo hbaseInfo = (HbaseInfo) targetInfo.getObject();
            initHbaseConnection(hbaseInfo, mongodbInfo.getMysqlUrl());
            initConnection(mongodbInfo);
            //增量数据需要切换到local数据库
            MongodbAddDataConfig config = (MongodbAddDataConfig) addDataConfig;
            if (config.getTs() == null) {
                config.setTs(new BsonTimestamp(0, 1));
            }
            if (oplogMongoDatabase != null) {
                //master/slave 架构下，日志信息在oplog.rs下面
                oplogCollection = oplogMongoDatabase.getCollection("oplog.rs");
                if (oplogCollection.count() < 1) {
                    //replica sets 架构下，日志信息在oplog.$main下面
                    oplogCollection = oplogMongoDatabase.getCollection("oplog.$main");
                }
            }
            mongoCollection = mongoDatabase.getCollection(mongodbInfo.getTableList().get(0));
            //$natural，-1为时间倒序，1为时间正序，查询时候去除n和db的操作
            String mongodbDatabase = mongodbInfo.getDatabase();
            List<String> getTableList = mongodbInfo.getTableList();
            CanalConnector connector = StringUtil.getConnector(config, hbaseInfo, mongodbInfo);
            List<TableInfo> tableInfoList = hbaseInfo.getTableInfoList();
            JSONArray columnJSONArray = new JSONArray();
            JSONArray hbaseColumnJSONArray = new JSONArray();
            JSONArray hbaseMysqlColumnJSONArray = new JSONArray();
            JSONArray mysqlColumnJSONArray = new JSONArray();
            JSONArray hbaseMongodbColumnJSONArray = new JSONArray();
            JSONArray mongoColumnJSONArray = new JSONArray();
            Set<String> tempSet = new HashSet<>();
            String subSql = "";
            String destination = config.getCanalDestination();
            for (TableInfo tableInfo : tableInfoList) {
                String getSql = tableInfo.getSql().replace(", ", ",");
                tempSet = StringUtil.getTableSet(getSql);
                columnJSONArray = tableInfo.getColumnJSONArray();
                hbaseColumnJSONArray = tableInfo.getHbaseColumnJSONArray();
                hbaseMysqlColumnJSONArray = tableInfo.getHbaseMysqlColumnJSONArray();
                mysqlColumnJSONArray = tableInfo.getMysqlColumnJSONArray();
                hbaseMongodbColumnJSONArray = tableInfo.getHbaseMongodbColumnJSONArray();
                mongoColumnJSONArray = tableInfo.getMongodbColumnJSONArray();
                subSql = tableInfo.getSql();
                if (tableInfo.getSql().indexOf(" limit ") != -1) {
                    subSql = tableInfo.getSql().substring(0, tableInfo.getSql().indexOf(" limit "));
                }
            }
            //获取当前表hbase中字段名
            while (true) {
                try {
                    Thread.sleep(AbstractWriteData.sleep);
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
                            LOGGER.info("mongo getWithoutAck获取connector第" + getWithoutAckCount + "次重试成功！");
                            getWithoutAckCount = 0;
                        }
                        break;
                    } catch (CanalClientException e) {
                        connector.disconnect();
                        Thread.sleep(ConstantSymbol.RETRY_SLEEP);
                        getWithoutAckCount++;
                        LOGGER.warn("mongo getWithoutAck获取connector第" + getWithoutAckCount + "次重试", e);
                        connector.connect();
                        if (getWithoutAckCount >= ConstantSymbol.RETRY_COUNT) {
                            LOGGER.error("mongo getWithoutAck获取connector超过20次", e);
                        }
                    }
                }
                if (batchId == -2) {
                    continue;
                }
                List<CanalEntry.Entry> entryList = message.getEntries();
                int size = entryList.size();
                if (batchId != -1 && size > 0) {
                    StringUtil.mongoAddMysqlAction(hbaseDds, entryList, tempSet, mongoCollection,
                        mysqlColumnJSONArray, hbaseMysqlColumnJSONArray, mongoColumnJSONArray, columnJSONArray,
                        hbaseColumnJSONArray, destination);
                }
                try {
                    connector.ack(batchId);
                } catch (CanalClientException e) {
                    connector.disconnect();
                    Thread.sleep(ConstantSymbol.RETRY_SLEEP);
                    connector.connect();
                    LOGGER.warn("mongo ack batchId " + batchId + " 时出错，重新获取connector");
                }
                //mongodb中数据变化
                if (oplogCollection != null) {
                    try {
                        StringUtil.mongoAddMongoAction(config, mongodbDatabase, getTableList, oplogCollection, hbaseDds,
                            mysqlDds, mongoColumnJSONArray, hbaseMongodbColumnJSONArray, subSql,
                            hbaseMysqlColumnJSONArray, columnJSONArray, hbaseColumnJSONArray, destination);
                    } catch (CommunicationsException e) {
                        initHbaseConnection(hbaseInfo, mongodbInfo.getMysqlUrl());
                        LOGGER.warn("mongo重新获取druid");
                    }
                }
                message = null;
                entryList = null;
                System.gc();
            }
        } catch (MongoCursorNotFoundException e) {
            LOGGER.error("游标超时！", e);
        } catch (Exception e) {
            LOGGER.error("mongo增量出错", e);
        }
    }

}

class EveryMongoSend implements Runnable {

    private TableInfo tableInfo;
    private MongodbInfo mongodbInfo;
    private DruidDataSource hbaseDds;
    private DruidDataSource mysqlDds;
    private final static Logger LOGGER = Logger.getLogger(EveryMongoSend.class);
    private int POOL_NUM = AbstractWriteData.POOL_NUM;
    private int maxActive = AbstractWriteData.maxActive;
    private MongoDatabase mongoDatabase;
    private int EVERY_COUNT_MAX = AbstractWriteData.EVERY_COUNT_MAX;
    private long keepAliveTime = AbstractWriteData.keepAliveTime;

    public EveryMongoSend(TableInfo tableInfo, MongodbInfo mongodbInfo, DruidDataSource hbaseDds, DruidDataSource
        mysqlDds, MongoDatabase mongoDatabase) {
        this.tableInfo = tableInfo;
        this.mongodbInfo = mongodbInfo;
        this.hbaseDds = hbaseDds;
        this.mysqlDds = mysqlDds;
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public void run() {
        String hbaseTable = tableInfo.getTable();
        try {
            Thread.currentThread().setName(this.getClass() + ConstantSymbol.SPLIT + hbaseTable);
            JSONArray hbaseColumnJSONArray = new JSONArray();
            JSONArray columnJSONArray = new JSONArray();
            String columnInfo = "";
            String columnFamily = tableInfo.getColumnFamily();
            if (null != tableInfo.getHbaseColumnJSONArray()) {
                columnJSONArray = tableInfo.getColumnJSONArray();
                hbaseColumnJSONArray = tableInfo.getHbaseColumnJSONArray();
                String pk = hbaseColumnJSONArray.getString(0);
                columnInfo = StringUtil.getCreateColumnInfo(hbaseColumnJSONArray, pk, columnFamily);
            }
            String mongodbTable = mongodbInfo.getTableList().get(0);
            Connection connection = hbaseDds.getConnection();
            String sql = tableInfo.getSql();
            //暂时支持一张表，多张表配置列名时不好配置
            String createSQL = "create table if not exists \"" + hbaseTable + "\"(" + columnInfo + ")";
            connection.prepareStatement(createSQL).executeUpdate();
            connection.commit();
            connection.close();
            // 存放返回的结果
            List<Future<String>> futures = new ArrayList<Future<String>>();
            long start = System.currentTimeMillis();
            //表名为空时传输全部表，不为空则传输指定表
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(mongodbTable);
            Connection mysqlCountConnection = mysqlDds.getConnection();
            int allCount = 0;
            String selectSQL = "select count(*) from (" + sql + ") temp";
            long startTime = System.currentTimeMillis();
            ResultSet resultSet = mysqlCountConnection.prepareStatement(selectSQL).executeQuery();
            LOGGER.info(selectSQL);
            resultSet.next();
            int count = resultSet.getInt(1);
            LOGGER.info("mongo总共数量：" + count);
            resultSet.close();
            long everyCount = count / (POOL_NUM - 1);
            long poolNum;
            if (everyCount > EVERY_COUNT_MAX) {
                everyCount = EVERY_COUNT_MAX;
                poolNum = (count / everyCount) + 1L;
            } else {
                everyCount = count;
                poolNum = 1;
            }
            LOGGER.info("poolNum:" + poolNum + ";everyCount:" + everyCount);
            ThreadPoolExecutor pool = new ThreadPoolExecutor(POOL_NUM, maxActive, keepAliveTime, TimeUnit
                .MILLISECONDS, new LinkedBlockingQueue<Runnable>((int) poolNum));
            for (long k = 0; k < poolNum; k++) {
                Connection hbaseConnection = hbaseDds.getConnection();
                Connection mysqlConnection = mysqlDds.getConnection();
                MongodbSendToHbase mongodbSendToHbase = new MongodbSendToHbase(mongoCollection, everyCount, k,
                    hbaseConnection, mysqlConnection, hbaseTable, hbaseColumnJSONArray, sql, columnJSONArray,
                    startTime, count);
                Future<String> future = pool.submit(mongodbSendToHbase);
                futures.add(future);
                mongodbSendToHbase = null;
            }
            mysqlCountConnection.close();
            pool.shutdown();
            //等待线程执行完
            allCount += StringUtil.getAllCount(futures);
            StringUtil.printSpeed(start, allCount, hbaseTable, LOGGER);
        } catch (SQLException e) {
            LOGGER.error("发送" + hbaseTable + "表的mongo数据时出错", e);
        }
    }

}

class MongodbSendToHbase implements Callable {

    private MongoCollection<Document> mongoCollection;
    private long everyCount;
    private long k;
    private Connection hbaseConnection;
    private Connection mysqlConnection;
    private String table;
    private JSONArray hbaseColumnJSONArray;
    private JSONArray columnJSONArray;
    private String sql;
    private long startTime;
    private int count;
    private final static Logger LOGGER = Logger.getLogger(WriteMongodbDataToHbase.class);

    MongodbSendToHbase(MongoCollection<Document> mongoCollection, long everyCount, long k, Connection
        hbaseConnection, Connection mysqlConnection, String table, JSONArray hbaseColumnJSONArray, String sql,
                       JSONArray columnJSONArray, long startTime, int count) {
        this.mongoCollection = mongoCollection;
        this.everyCount = everyCount;
        this.k = k;
        this.hbaseConnection = hbaseConnection;
        this.mysqlConnection = mysqlConnection;
        this.table = table;
        this.hbaseColumnJSONArray = hbaseColumnJSONArray;
        this.columnJSONArray = columnJSONArray;
        this.sql = sql;
        this.startTime = startTime;
        this.count = count;
    }

    @Override
    public Object call() {
        int allCount = 0;
        int skip = (int) everyCount * (int) k;
        String offset = " offset ";
        String limit = " limit ";
        if (sql.indexOf(offset) != -1) {
            int offsetSql = Integer.valueOf(sql.substring(sql.indexOf(offset) + offset.length()));
            skip += offsetSql;
            count += offsetSql;
        }
        String subSql = sql;
        if (sql.indexOf(limit) != -1) {
            subSql = sql.substring(0, sql.indexOf(limit));
        }
        Thread.currentThread().setName(this.getClass() + ConstantSymbol.SPLIT + skip + ConstantSymbol.SPLIT +
                                       everyCount);
        int subCount = count - skip;
        if (subCount < everyCount) {
            everyCount = subCount;
        }
        String selectSQL = subSql + " limit " + everyCount + " offset " + skip;
        try {
            ResultSet resultSet = mysqlConnection.prepareStatement(selectSQL).executeQuery();
            //组装sql，根据hbasecolumn添加?的个数
            StringBuilder upsertSB = new StringBuilder("upsert into \"" + table + "\" values(");
            for (int i = 0, size = hbaseColumnJSONArray.size(); i < size; i++) {
                upsertSB.append("?,");
            }
            String upsertS = upsertSB.substring(0, upsertSB.length() - 1) + ")";
            PreparedStatement stmt = hbaseConnection.prepareStatement(upsertS);
            while (resultSet.next()) {
                boolean isLose = false;
                if (isLose) {
                    String id = resultSet.getString(1);
                    String dcw_id = resultSet.getString(3);
                    String selectLose = "select count(distinct \"dcw_id\") from \"" + this.table + "\" where " +
                                        "\"dcw_id\" = \'" + dcw_id + "\'";
                    ResultSet rs = hbaseConnection.prepareStatement(selectLose).executeQuery();
                    boolean isNext = false;
                    while (rs.next()) {
                        isNext = true;
                        if (rs.getInt(1) < 1) {
                            LOGGER.info("have-next:" + dcw_id + "-" + id);
                            allCount += resultSetNext(id, resultSet, stmt);
                        }
                    }
                    if (!isNext) {
                        LOGGER.info("no-next:" + dcw_id + "-" + id);
                        allCount += resultSetNext(id, resultSet, stmt);
                    }
                    rs.close();
                    id = null;
                } else {
                    String id = resultSet.getString(1);
                    allCount += resultSetNext(id, resultSet, stmt);
                    id = null;
                }
            }
            upsertSB = null;
            stmt.close();
            resultSet.close();
            hbaseConnection.commit();
            resultSet = null;
            selectSQL = null;
            mongoCollection = null;
            mysqlConnection.close();
            hbaseConnection.close();
            LOGGER.info(table + ConstantSymbol.SPLIT + "发送完：" + skip + ConstantSymbol.SPLIT + everyCount);
            System.gc();
        } catch (Exception e) {
            LOGGER.error("error:" + selectSQL, e);
        }
        JSONObject json = new JSONObject();
        json.put("allCount", allCount);
        return json.toJSONString();
    }

    public int resultSetNext(String id, ResultSet resultSet, PreparedStatement stmt) throws Exception {
        int allCount = 0;
        if (StringUtils.isNotBlank(id)) {
            Bson bson = new BasicDBObject("_id", new ObjectId(id));
            FindIterable<Document> findIterable = mongoCollection.find(bson);
            //如果有mongo的数据则数量按照mongo来
            Document document = new Document();
            if (mongoCollection.count(bson) > 0) {
                for (Document doc : findIterable) {
                    document = doc;
                    doc = null;
                    break;
                }
            }
            //mongo要有数据就联合一起插入，只有mysql的数据也会插入
            if (document.size() > 0) {
                String jsonStr = document.toJson();
                List<JSONObject> splitList = StringUtil.splitCaseMongoData(jsonStr, columnJSONArray);
                int mongoCount = splitList.size();
                if (mongoCount < 1) {
                    sendToHbase(resultSet, new JSONObject(), stmt);
                    mongoCount = 1;
                } else {
                    for (JSONObject json : splitList) {
                        sendToHbase(resultSet, json, stmt);
                    }
                }
                allCount += mongoCount;
                jsonStr = null;
                splitList = null;
            } else {
                allCount += 1;
                sendToHbase(resultSet, new JSONObject(), stmt);
            }
            findIterable = null;
            bson = null;
            document = null;
        } else {
            allCount += 1;
            sendToHbase(resultSet, new JSONObject(), stmt);
        }
        return allCount;
    }

    public void sendToHbase(ResultSet resultSet, JSONObject jsonObject, PreparedStatement stmt) throws Exception {
        ResultSetMetaData metaData = resultSet.getMetaData();
        //第一个为ref_id作为objectId_timestamp
        String objectIdValue = resultSet.getObject(1).toString();
        String objectIdTimestampValue = StringUtil.getOrder(objectIdValue);
        jsonObject.put(metaData.getColumnLabel(1), objectIdTimestampValue);
        jsonObject.put("_id.$oid", objectIdValue);
        //从第二个开始
        for (int j = 2, columnCount = metaData.getColumnCount(); j <= columnCount; j++) {
            jsonObject.put(metaData.getColumnLabel(j), StringUtil.dealFormatDateTime(resultSet.getObject(j)));
        }
        Set<String> keySet = jsonObject.keySet();
        for (int i = 0, size = columnJSONArray.size(); i < size; i++) {
            String column = columnJSONArray.getString(i);
            int i1 = i + 1;
            if (keySet.contains(column)) {
                stmt.setString(i1, jsonObject.getString(column));
            } else {
                stmt.setString(i1, "");
            }
        }
        stmt.execute();
        jsonObject = null;
        keySet = null;
        objectIdValue = null;
        objectIdTimestampValue = null;
        metaData = null;
    }

}
