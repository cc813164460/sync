package com.hsgene;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.hbase.model.HbaseInfo;
import com.hsgene.hbase.model.TableInfo;
import com.hsgene.kafka.model.KafkaInfo;
import com.hsgene.model.TargetInfo;
import com.hsgene.model.UrlInfo;
import com.hsgene.model.UrlInfos;
import com.hsgene.mongodb.model.MongodbAddDataConfig;
import com.hsgene.mongodb.model.MongodbInfo;
import com.hsgene.mysql.model.MysqlAddDataConfig;
import com.hsgene.mysql.model.MysqlInfo;
import com.hsgene.mysql.util.WriteMysqlData;
import com.hsgene.util.StringUtil;
import com.hsgene.util.WriteData;
import org.apache.log4j.Logger;
import org.bson.BsonTimestamp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 14:58 2017/10/26
 * @Modified By:
 */
public class SyncToolMain {

    private static final String URLINFOS_PROPERTIES = ConstantSymbol.URLINFOS_PROPERTIES;
    private final static Logger LOGGER = Logger.getLogger(SyncToolMain.class);

    public static void main(String[] args) {
        try {
            JSONObject jsonObject = StringUtil.getJSONObject(URLINFOS_PROPERTIES);
            UrlInfos urlInfos = new UrlInfos();
            List<UrlInfo> urlInfoList = new ArrayList<>();
            JSONArray mongodbJSONArray = jsonObject.getJSONArray(ConstantSymbol.MONGODB);
            if (mongodbJSONArray != null) {
                for (int i = 0, length = mongodbJSONArray.size(); i < length; i++) {
                    JSONObject mongodbJSONObject = mongodbJSONArray.getJSONObject(i);
                    UrlInfo urlInfo = new UrlInfo();
                    urlInfo.setDataType(ConstantSymbol.MONGODB);
                    TargetInfo targetInfo = new TargetInfo();
                    setTargetInfo(targetInfo, mongodbJSONObject);
                    urlInfo.setTargetInfo(targetInfo);
                    MongodbInfo mongodbInfo = new MongodbInfo();
                    mongodbInfo.setHost(mongodbJSONObject.getString("host"));
                    mongodbInfo.setPort(mongodbJSONObject.getInteger("port"));
                    mongodbInfo.setAuthDatabase(mongodbJSONObject.getString("auth.database"));
                    mongodbInfo.setMechanism(mongodbJSONObject.getString("mechanism"));
                    mongodbInfo.setDatabase(mongodbJSONObject.getString("database"));
                    mongodbInfo.setUser(StringUtil.getString(mongodbJSONObject.get("user")));
                    mongodbInfo.setPassword(StringUtil.getString(mongodbJSONObject.get("password")));
                    mongodbInfo.setTables(StringUtil.getString(mongodbJSONObject.get("tables")));
                    mongodbInfo.setOplogDatabase(StringUtil.getString(mongodbJSONObject.get("oplog.database")));
                    mongodbInfo.setSnapshotMysqlUrl(StringUtil.getString(mongodbJSONObject.get(ConstantSymbol
                        .SNAPSHOT_MYSQL_URL)));
                    urlInfo.setDatabaseInfo(mongodbInfo);
                    Object ts = mongodbJSONObject.get("ts");
                    urlInfo.setSyncType(StringUtil.getString(mongodbJSONObject.get("sync.type")));
                    mongodbInfo.setMysqlUrl(StringUtil.getString(mongodbJSONObject.get("mysql.url")));
                    MongodbAddDataConfig mongodbAddDataConfig = new MongodbAddDataConfig();
                    int seconds = 0;
                    int inc = 1;
                    if (ts.toString().indexOf(ConstantSymbol.TSSPLIT) != -1) {
                        String[] strs = ts.toString().split(ConstantSymbol.TSSPLIT);
                        if (StringUtil.isInteger(strs[0])) {
                            strs[0] = strs[0].replace(" ", "");
                            seconds = Integer.valueOf(strs[0]);
                        }
                        if (strs.length > 1) {
                            strs[1] = strs[1].replace(" ", "");
                            inc = StringUtil.isInteger(strs[1]) ? Integer.valueOf(strs[1]) : 1;
                        }
                    }
                    mongodbAddDataConfig.setTs(new BsonTimestamp(seconds, inc));
                    Object canalZkServersO = mongodbJSONObject.get(ConstantSymbol.CANAL_ZKSERVERS);
                    Object canalHostO = mongodbJSONObject.get(ConstantSymbol.CANAL_HOST);
                    Object canalPortO = mongodbJSONObject.get(ConstantSymbol.CANAL_PORT);
                    Object canalDestinationO = mongodbJSONObject.get(ConstantSymbol.CANAL_DESTINATION);
                    boolean flag1 = canalHostO == null || canalPortO == null;
                    boolean flag2 = canalZkServersO == null;
                    if (flag1 && flag2) {
                        throw new IllegalArgumentException("canal.zkServers没有配置或者canal.host以及canal.port没有配置");
                    }
                    if (canalDestinationO == null) {
                        throw new IllegalArgumentException("canal.destination目标文件没有配置");
                    }
                    mongodbAddDataConfig.setCanalZkServers(mongodbJSONObject.getString(ConstantSymbol.CANAL_ZKSERVERS));
                    mongodbAddDataConfig.setCanalDestination(mongodbJSONObject.getString(ConstantSymbol
                        .CANAL_DESTINATION));
                    mongodbAddDataConfig.setCanalHost(mongodbJSONObject.getString(ConstantSymbol.CANAL_HOST));
                    mongodbAddDataConfig.setCanalPort(mongodbJSONObject.getInteger(ConstantSymbol.CANAL_PORT));
                    urlInfo.setAddDataConfig(mongodbAddDataConfig);
                    urlInfoList.add(urlInfo);
                }
            }
            JSONArray mysqlJSONArray = jsonObject.getJSONArray(ConstantSymbol.MYSQL);
            if (mysqlJSONArray != null) {
                for (int i = 0, length = mysqlJSONArray.size(); i < length; i++) {
                    JSONObject mysqlJSONObject = mysqlJSONArray.getJSONObject(i);
                    UrlInfo urlInfo = new UrlInfo();
                    urlInfo.setDataType(ConstantSymbol.MYSQL);
                    TargetInfo targetInfo = new TargetInfo();
                    setTargetInfo(targetInfo, mysqlJSONObject);
                    urlInfo.setTargetInfo(targetInfo);
                    MysqlInfo mysqlInfo = new MysqlInfo();
                    mysqlInfo.setHost(mysqlJSONObject.getString("host"));
                    mysqlInfo.setPort(mysqlJSONObject.getInteger("port"));
                    mysqlInfo.setDatabase(mysqlJSONObject.getString("database"));
                    mysqlInfo.setUser(StringUtil.getString(mysqlJSONObject.get("user")));
                    mysqlInfo.setPassword(StringUtil.getString(mysqlJSONObject.get("password")));
                    mysqlInfo.setSnapshotMysqlUrl(StringUtil.getString(mysqlJSONObject.get(ConstantSymbol
                        .SNAPSHOT_MYSQL_URL)));
                    urlInfo.setSyncType(StringUtil.getString(mysqlJSONObject.get("sync.type")));
                    urlInfo.setDatabaseInfo(mysqlInfo);
                    MysqlAddDataConfig mysqlAddDataConfig = new MysqlAddDataConfig();
                    Object canalZkServersO = mysqlJSONObject.get(ConstantSymbol.CANAL_ZKSERVERS);
                    Object canalHostO = mysqlJSONObject.get(ConstantSymbol.CANAL_HOST);
                    Object canalPortO = mysqlJSONObject.get(ConstantSymbol.CANAL_PORT);
                    Object canalDestinationO = mysqlJSONObject.get(ConstantSymbol.CANAL_DESTINATION);
                    boolean flag1 = canalHostO == null || canalPortO == null;
                    boolean flag2 = canalZkServersO == null;
                    if (flag1 && flag2) {
                        throw new IllegalArgumentException("canal.zkServers没有配置或者canal.host以及canal.port没有配置");
                    }
                    if (canalDestinationO == null) {
                        throw new IllegalArgumentException("canal.destination目标文件没有配置");
                    }
                    mysqlAddDataConfig.setCanalZkServers(mysqlJSONObject.getString(ConstantSymbol.CANAL_ZKSERVERS));
                    mysqlAddDataConfig.setCanalDestination(mysqlJSONObject.getString(ConstantSymbol.CANAL_DESTINATION));
                    mysqlAddDataConfig.setCanalHost(mysqlJSONObject.getString(ConstantSymbol.CANAL_HOST));
                    mysqlAddDataConfig.setCanalPort(mysqlJSONObject.getInteger(ConstantSymbol.CANAL_PORT));
                    urlInfo.setAddDataConfig(mysqlAddDataConfig);
                    urlInfoList.add(urlInfo);
                }
            }
            WriteData writeData = new WriteMysqlData();
            urlInfos.setUrlInfoList(urlInfoList);
            writeData.writeData(urlInfos);
            LOGGER.info("执行结束");
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private static void setTargetInfo(TargetInfo targetInfo, JSONObject jsonObject) {
        JSONObject targetInfoJSONObject = jsonObject.getJSONObject("target.info");
        switch (targetInfoJSONObject.getString("type")) {
            case ConstantSymbol.KAFKA:
                targetInfo.setType(ConstantSymbol.KAFKA);
                KafkaInfo kafkaInfo = new KafkaInfo();
                kafkaInfo.setTopic(targetInfoJSONObject.getString("topic"));
                kafkaInfo.setMetadataBrokerList(targetInfoJSONObject.getString("metadata.broker"));
                kafkaInfo.setZookeeperConnect(targetInfoJSONObject.getString("zookeeper.connect"));
                targetInfo.setObject(kafkaInfo);
                break;
            case ConstantSymbol.HBASE:
                targetInfo.setType(ConstantSymbol.HBASE);
                HbaseInfo hbaseInfo = new HbaseInfo();
                hbaseInfo.setUrl(targetInfoJSONObject.getString("url"));
                List<TableInfo> tableInfoList = new ArrayList<>();
                JSONArray tableInfoArray = targetInfoJSONObject.getJSONArray("table.info");
                for (int i = 0, length = tableInfoArray.size(); i < length; i++) {
                    JSONObject tableInfoObject = tableInfoArray.getJSONObject(i);
                    TableInfo tableInfo = new TableInfo();
                    tableInfo.setHbaseColumns(tableInfoObject.getString("hbase.columns"));
                    tableInfo.setSql(tableInfoObject.getString("sql"));
                    tableInfo.setTable(tableInfoObject.getString("table"));
                    tableInfo.setMongodbColumns(tableInfoObject.getString("mongodb.columns"));
                    tableInfo.setColumnFamily(tableInfoObject.getString("column.family"));
                    tableInfoList.add(tableInfo);
                }
                hbaseInfo.setTableInfoList(tableInfoList);
                targetInfo.setObject(hbaseInfo);
                break;
            default:
                break;
        }
    }

}
