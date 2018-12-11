package com.hsgene.mongodb.util;

import com.alibaba.fastjson.JSONObject;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.kafka.model.KafkaInfo;
import com.hsgene.kafka.util.KafkaProducerSender;
import com.hsgene.model.*;
import com.hsgene.mongodb.model.MongodbAddDataConfig;
import com.hsgene.mongodb.model.MongodbInfo;
import com.hsgene.util.SaveFlag;
import com.hsgene.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import kafka.producer.KeyedMessage;
import org.apache.log4j.Logger;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:11 2017/10/19
 * @Modified By:
 */
public class WriteMongodbDataToKafka extends WriteMongodbData {

    private final static Logger LOGGER = Logger.getLogger(WriteMongodbDataToKafka.class);
    private String topic = "mongodb_topic";

    @Override
    public void writeAllData(DatabaseInfo databaseInfo, TargetInfo targetInfo) {
        try {
            KafkaInfo kafkaInfo = (KafkaInfo) targetInfo.getObject();
            MongodbInfo mongodbInfo = (MongodbInfo) databaseInfo;
            initConnection(mongodbInfo);
            topic = kafkaInfo.getTopic();
            SendFlag sendFlag = new SendFlag();
            sendFlag.setTargetInfo(targetInfo);
            sendFlag.setUrl(mongodbInfo.getHost() + ":" + mongodbInfo.getPort());
            sendFlag.setDatabase(mongodbInfo.getDatabase());
            sendFlag.setTable(mongodbInfo.getTables());
            sendFlag.setType(ConstantSymbol.MONGODB);
            KafkaProducerSender sender = new KafkaProducerSender(kafkaInfo);
            MongoIterable<String> collectionNames = mongoDatabase.listCollectionNames();
            //master/slave 架构下，日志信息在oplog.rs下面
            MongoCollection<Document> oplogCollection = oplogMongoDatabase.getCollection("oplog.rs");
            if (oplogCollection.count() < 1) {
                //replica sets 架构下，日志信息在oplog.$main下面
                oplogCollection = oplogMongoDatabase.getCollection("oplog.$main");
            }
            BsonTimestamp lastTs = (BsonTimestamp) oplogCollection.find().skip((int) oplogCollection.count() - 1)
                .limit(1).iterator().next().get("ts");
            ExecutorService pool = new ThreadPoolExecutor(POOL_NUM, 200, 0L, TimeUnit.MILLISECONDS, new
                LinkedBlockingQueue<Runnable>());
            List<KeyedMessage<String, String>> keyedMessageList = new ArrayList<>();
            MongodbAddDataConfig tempAddDataConfig = new MongodbAddDataConfig();
            tempAddDataConfig.setTs(lastTs);
            for (String collectionName : collectionNames) {
                //表名为空时传输全部表，不为空则传输指定表
                List<String> getTables = mongodbInfo.getTableList();
                if (getTables.size() == 0 || (getTables.size() != 0 && getTables.contains(collectionName))) {
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);
                    FindIterable<Document> findIterable = mongoCollection.find();
                    DataSendToKafka dataSendToKafka;
                    for (Document document : findIterable) {
                        dataSendToKafka = new DataSendToKafka();
                        dataSendToKafka.setSource(mongodbInfo.getHost());
                        dataSendToKafka.setAction(ConstantSymbol.ACTION_TYPE_INSERT);
                        StringBuilder databaseAndCollection = new StringBuilder(mongodbInfo.getDatabase());
                        databaseAndCollection.append(".");
                        databaseAndCollection.append(collectionName);
                        dataSendToKafka.setTable(databaseAndCollection.toString());
                        dataSendToKafka.setData(document.toJson());
                        keyedMessageList.add(new <String, String>KeyedMessage(topic, dataSendToKafka.toString()));
                        if (keyedMessageList.size() >= EVERY_COUNT_MAX) {
                            sender.send(keyedMessageList, pool);
                            keyedMessageList = new ArrayList<>();
                        }
                    }
                }
            }
            //最后发送
            sender.send(keyedMessageList, pool);
            //阻止新来的任务提交，对已经提交了的任务不会产生任何影响。当已经提交的任务执行完后，它会将那些闲置的线程（idleWorks）进行中断，这个过程是异步的
            pool.shutdown();
            //等待线程执行完
            while (!pool.isTerminated()) {
            }
            sender.close();
            //写入文件保存位置
            sendFlag.setAddDataConfig(tempAddDataConfig);
            SaveFlag.setSendFlag(sendFlag);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    @Override
    public void writeAddData(DatabaseInfo databaseInfo, TargetInfo targetInfo, AddDataConfig addDataConfig) {
        try {
            KafkaInfo kafkaInfo = (KafkaInfo) targetInfo.getObject();
            MongodbInfo mongodbInfo = (MongodbInfo) databaseInfo;
            SendFlag sendFlag = new SendFlag();
            sendFlag.setTargetInfo(targetInfo);
            sendFlag.setUrl(mongodbInfo.getHost() + ":" + mongodbInfo.getPort());
            sendFlag.setDatabase(mongodbInfo.getDatabase());
            sendFlag.setTable(mongodbInfo.getTables());
            sendFlag.setType(ConstantSymbol.MONGODB);
            //增量数据需要切换到local数据库
            initConnection(mongodbInfo);
            topic = kafkaInfo.getTopic();
            KafkaProducerSender sender = new KafkaProducerSender(kafkaInfo);
            MongodbAddDataConfig mongodbAddDataConfig = (MongodbAddDataConfig) addDataConfig;
            if (mongodbAddDataConfig.getTs() == null) {
                mongodbAddDataConfig.setTs(new BsonTimestamp(0, 1));
            }
            //master/slave 架构下，日志信息在oplog.rs下面
            MongoCollection<Document> oplogCollection = oplogMongoDatabase.getCollection("oplog.rs");
            if (oplogCollection.count() < 1) {
                //replica sets 架构下，日志信息在oplog.$main下面
                oplogCollection = oplogMongoDatabase.getCollection("oplog.$main");
            }
            //$natural，-1为时间倒序，1为时间正序，查询时候去除n和db的操作
            String getDatabase = mongodbInfo.getDatabase();
            List<String> getTableList = mongodbInfo.getTableList();
            String getHost = mongodbInfo.getHost();
            BsonTimestamp lastTs = new BsonTimestamp(0, 1);
            while (true) {
                Bson filters = StringUtil.getFilters(mongodbAddDataConfig, getDatabase, getTableList, "$gt");
                FindIterable<Document> tempDocuments = oplogCollection.find(filters).sort(new BasicDBObject
                    ("$natural", -1)).limit(1);
                Document tempDocument = new Document();
                for (Document document : tempDocuments) {
                    tempDocument = document;
                    break;
                }
                if (tempDocument.size() > 0 && lastTs.compareTo((BsonTimestamp) tempDocument.get("ts")) != 0) {
                    FindIterable<Document> documents = oplogCollection.find(filters).sort(new BasicDBObject
                        ("$natural", 1));
                    if (!documents.iterator().hasNext()) {
                        LOGGER.info("没有日志信息");
                    }
                    List<KeyedMessage<String, String>> keyedMessageList = new ArrayList<>();
                    DataSendToKafka dataSendToKafka;
                    ExecutorService pool = new ThreadPoolExecutor(POOL_NUM, 200, 0L, TimeUnit.MILLISECONDS, new
                        LinkedBlockingQueue<Runnable>());
                    MongodbAddDataConfig tempAddDataConfig = new MongodbAddDataConfig();
                    tempAddDataConfig.setTs(lastTs);
                    for (Document document : documents) {
                        String databaseAndTable = document.getString("ns");
                        String[] strs = databaseAndTable.split("\\.");
                        String database = strs[0];
                        String table = strs[1];
                        //数据库名为空时传输全部数据库，不为空则传输指定数据库
                        if (getDatabase.length() == 0 || (getDatabase.length() != 0 && database.equals(getDatabase))) {
                            //表名为空时传输全部表，不为空则传输指定表
                            if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(table)
                            )) {
                                dataSendToKafka = new DataSendToKafka();
                                dataSendToKafka.setSource(getHost);
                                Object op = document.get("op");
                                String action = StringUtil.getAction(op);
                                Document documentValue = (Document) document.get("o");
                                lastTs = (BsonTimestamp) document.get("ts");
                                tempAddDataConfig.setTs(lastTs);
                                if ("c".equals(op)) {
                                    if (documentValue.containsKey("renameCollection")) {
                                        action = "alter";
                                        dataSendToKafka.setTable(documentValue.getString("renameCollection"));
                                        dataSendToKafka.setData(documentValue.getString("to"));
                                    } else if (documentValue.containsKey("create")) {
                                        action = "create";
                                        dataSendToKafka.setTable(documentValue.getString("create"));
                                    } else if (documentValue.containsKey("drop")) {
                                        action = "drop";
                                        dataSendToKafka.setTable(documentValue.getString("drop"));
                                    }
                                } else {
                                    JSONObject jsonObject = JSONObject.parseObject(documentValue.toJson());
                                    dataSendToKafka.setTable(databaseAndTable);
                                    dataSendToKafka.setData(jsonObject);
                                }
                                dataSendToKafka.setAction(action);
                                keyedMessageList.add(new <String, String>KeyedMessage(topic, dataSendToKafka.toString
                                    ()));
                                if (keyedMessageList.size() >= EVERY_COUNT_MAX) {
                                    sender.send(keyedMessageList, pool);
                                    tempAddDataConfig.setTs(lastTs);
                                    sendFlag.setAddDataConfig(tempAddDataConfig);
                                    SaveFlag.setSendFlag(sendFlag);
                                    keyedMessageList = new ArrayList<>();
                                }
                            }
                        }
                    }
                    //最后发送
                    sender.send(keyedMessageList, pool);
                    //阻止新来的任务提交，对已经提交了的任务不会产生任何影响。当已经提交的任务执行完后，它会将那些闲置的线程（idleWorks）进行中断，这个过程是异步的
                    pool.shutdown();
                    //等待线程执行完
                    while (!pool.isTerminated()) {
                    }
                    tempAddDataConfig.setTs(lastTs);
                    //写入文件保存位置
                    sendFlag.setAddDataConfig(tempAddDataConfig);
                    SaveFlag.setSendFlag(sendFlag);
                    //作为常驻进程使用，不用考虑容错
                    mongodbAddDataConfig.setTs(lastTs);
                    documents = null;
                }
                filters = null;
                tempDocuments = null;
                tempDocument = null;
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }
}