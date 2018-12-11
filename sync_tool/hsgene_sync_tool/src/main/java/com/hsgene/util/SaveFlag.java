package com.hsgene.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.hbase.model.HbaseInfo;
import com.hsgene.kafka.model.KafkaInfo;
import com.hsgene.model.SendFlag;
import com.hsgene.model.TargetInfo;
import com.hsgene.mongodb.model.MongodbAddDataConfig;
import com.hsgene.mysql.model.MysqlAddDataConfig;
import org.apache.log4j.Logger;
import org.bson.BsonTimestamp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:08 2017/12/12
 * @Modified By:
 */
public class SaveFlag {

    private final static Logger LOGGER = Logger.getLogger(SaveFlag.class);
    private final static String filename = ConstantSymbol.URLINFOS_PROPERTIES;

    public static synchronized void setSendFlag(SendFlag sendFlag) {
        try {
            String propertiesPath = StringUtil.getAbsolutePath(filename);
            FileInputStream fileInputStream = new FileInputStream(propertiesPath);
            StringBuffer stringBuffer = StringUtil.fileInputStreamToStringBuffer(fileInputStream);
            JSONObject jsonObject = JSONObject.parseObject(stringBuffer.toString());
            String type = sendFlag.getType();
            JSONArray typeJSONArray = jsonObject.getJSONArray(type);
            TargetInfo targetInfo = sendFlag.getTargetInfo();
            for (int i = 0, length = typeJSONArray.size(); i < length; i++) {
                JSONObject dataJsonObject = typeJSONArray.getJSONObject(i);
                JSONObject targetInfoJSONObject = dataJsonObject.getJSONObject("target.info");
                String targetType = StringUtil.getString(targetInfoJSONObject.getString("type"));
                boolean flag = false;
                switch (targetType) {
                    case ConstantSymbol.KAFKA:
                        String metadataBroker = StringUtil.getString(targetInfoJSONObject.getString("metadata.broker"));
                        String zookeeperConnect = StringUtil.getString(targetInfoJSONObject.getString("zookeeper" +
                                                                                                      ".connect"));
                        KafkaInfo kafkaInfo = (KafkaInfo) targetInfo.getObject();
                        flag = targetType.equals(targetInfo.getType()) && metadataBroker.equals(kafkaInfo
                            .getMetadataBrokerList()) && zookeeperConnect.equals(kafkaInfo.getZookeeperConnect());
                        break;
                    case ConstantSymbol.HBASE:
                        String url = StringUtil.getString(targetInfoJSONObject.getString("url"));
                        HbaseInfo hbaseInfo = (HbaseInfo) targetInfo.getObject();
                        flag = targetType.equals(targetInfo.getType()) && url.equals(hbaseInfo.getUrl());
                        break;
                    default:
                        break;
                }
                String url = StringUtil.getString(dataJsonObject.getString("host")) + ":" + StringUtil.getString
                    (dataJsonObject.getString("port"));
                String database = StringUtil.getString(dataJsonObject.getString("database"));
                if (flag && url.equals(sendFlag.getUrl()) && database.equals(sendFlag.getDatabase())) {
                    if ("mysql".equals(type)) {
                        MysqlAddDataConfig mysqlAddDataConfig = (MysqlAddDataConfig) sendFlag.getAddDataConfig();
                        dataJsonObject.put("ts", mysqlAddDataConfig.getTs());
                        String logName = mysqlAddDataConfig.getEndLogName();
                        if (logName != null) {
                            dataJsonObject.put("end.log.name", logName);
                            dataJsonObject.put("end.log.pos", mysqlAddDataConfig.getEndLogPos());
                        }
                    } else if ("mongodb".equals(type)) {
                        MongodbAddDataConfig mongodbAddDataConfig = (MongodbAddDataConfig) sendFlag.getAddDataConfig();
                        BsonTimestamp ts = mongodbAddDataConfig.getTs();
                        dataJsonObject.put("ts", ts.getTime() + ConstantSymbol.TSSPLIT + ts.getInc());
                    }
                }
                typeJSONArray.set(i, dataJsonObject);
            }
            jsonObject.put(type, typeJSONArray);
            FileWriter writer = new FileWriter(propertiesPath, false);
            writer.write(StringUtil.formatJSON(jsonObject.toJSONString()));
            writer.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    public static synchronized void replaceTs(BsonTimestamp bt) {
        try {
            String propertiesPath = StringUtil.getAbsolutePath(filename);
            FileInputStream fileInputStream = new FileInputStream(propertiesPath);
            InputStreamReader rdCto = new InputStreamReader(fileInputStream);
            BufferedReader bfReader = new BufferedReader(rdCto);
            String txtLine = null;
            StringBuilder sb = new StringBuilder();
            while ((txtLine = bfReader.readLine()) != null) {
                if (txtLine.indexOf("\"ts\":") != -1) {
                    String tsLine = "\"ts\":\"" + bt.getTime() + "-" + bt.getInc() + "\"";
                    if(txtLine.endsWith(",")) {
                        txtLine = tsLine + ",";
                    } else {
                        txtLine = tsLine;
                    }
                }
                sb.append(txtLine + "\n");
            }
            fileInputStream.close();
            rdCto.close();
            bfReader.close();
            FileWriter writer = new FileWriter(propertiesPath, false);
            writer.write(sb.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    public static synchronized void setSyncTypeToAdd() {
        try {
            String propertiesPath = StringUtil.getAbsolutePath(filename);
            FileInputStream fileInputStream = new FileInputStream(propertiesPath);
            InputStreamReader rdCto = new InputStreamReader(fileInputStream);
            BufferedReader bfReader = new BufferedReader(rdCto);
            String txtLine = null;
            StringBuilder sb = new StringBuilder();
            while ((txtLine = bfReader.readLine()) != null) {
                if (txtLine.indexOf("\"sync.type\":") != -1) {
                    String tsLine = "\"sync.type\":\"" + ConstantSymbol.SYNC_ADD + "\"";
                    if(txtLine.endsWith(",")) {
                        txtLine = tsLine + ",";
                    } else {
                        txtLine = tsLine;
                    }
                }
                sb.append(txtLine + "\n");
            }
            fileInputStream.close();
            rdCto.close();
            bfReader.close();
            FileWriter writer = new FileWriter(propertiesPath, false);
            writer.write(sb.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }
}
