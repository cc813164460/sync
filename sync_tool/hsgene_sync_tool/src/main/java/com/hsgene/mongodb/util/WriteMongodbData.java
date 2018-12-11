package com.hsgene.mongodb.util;

import com.hsgene.constant.ConstantSymbol;
import com.hsgene.model.AddDataConfig;
import com.hsgene.model.DatabaseInfo;
import com.hsgene.model.TargetInfo;
import com.hsgene.mongodb.model.MongodbInfo;
import com.hsgene.util.AbstractWriteData;
import com.hsgene.util.StringUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:22 2017/12/12
 * @Modified By:
 */
public class WriteMongodbData extends AbstractWriteData {

    private final static Logger LOGGER = Logger.getLogger(WriteMongodbData.class);

    MongoDatabase mongoDatabase = null;
    MongoDatabase oplogMongoDatabase = null;
    MongoCollection oplogCollection = null;

    public MongoDatabase initConnection(MongodbInfo mongodbInfo) {
        try {
            String oplogDatabase = mongodbInfo.getOplogDatabase();
            MongoClient mongoClient = new MongoClient();
            //连接到MongoDB服务 如果是远程连接可以替换“localhost”为服务器所在IP地址
            //ServerAddress()两个参数分别为 服务器地址 和 端口
            if (StringUtil.isNullOrBlank(mongodbInfo.getDatabase()) || StringUtil.isNullOrBlank(mongodbInfo.getUser()
            ) || StringUtil.isNullOrBlank(mongodbInfo.getPassword())) {
                // 连接到 mongodb 服务
                mongoClient = new MongoClient(mongodbInfo.getHost(), mongodbInfo.getPort());
                // 连接到数据库
                mongoDatabase = mongoClient.getDatabase(mongodbInfo.getDatabase());
            } else {
                ServerAddress serverAddress = new ServerAddress(mongodbInfo.getHost(), mongodbInfo.getPort());
                List<ServerAddress> addrs = new ArrayList<ServerAddress>();
                addrs.add(serverAddress);
                String authDatabase = mongodbInfo.getAuthDatabase();
                //MongoCredential.create三个参数分别为 用户名 数据库名称 密码
                MongoCredential credential = null;
                String mechanism = mongodbInfo.getMechanism();
                switch (mechanism) {
                    case ConstantSymbol.MONGODB_CR:
                        credential = MongoCredential.createMongoCRCredential(mongodbInfo.getUser(), authDatabase,
                            mongodbInfo.getPassword().toCharArray());
                        break;
                    case ConstantSymbol.SCRAM_SHA_1:
                        credential = MongoCredential.createScramSha1Credential(mongodbInfo.getUser(), authDatabase,
                            mongodbInfo.getPassword().toCharArray());
                        break;
                }
                List<MongoCredential> credentials = new ArrayList<>();
                credentials.add(credential);
                MongoClientOptions.Builder build = new MongoClientOptions.Builder();
                //与目标数据库能够建立的最大connection数量为50
                build.connectionsPerHost(50);
                //0就是没有限制
                build.socketTimeout(0);
                build.socketKeepAlive(false);
                //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
                build.threadsAllowedToBlockForConnectionMultiplier(50);
                build.maxWaitTime(maxWait);
                //与数据库建立连接的timeout设置为1分钟
                build.connectTimeout(maxWait);
                MongoClientOptions options = build.build();
                //通过连接认证获取MongoDB连接
                mongoClient = new MongoClient(addrs, credentials, options);
                //连接到数据库
                mongoDatabase = mongoClient.getDatabase(mongodbInfo.getDatabase());
            }
            if (StringUtils.isNotBlank(oplogDatabase)) {
                long begin = System.currentTimeMillis();
                while (System.currentTimeMillis() - begin < 30000 && oplogMongoDatabase == null) {
                    oplogMongoDatabase = mongoClient.getDatabase(mongodbInfo.getOplogDatabase());
                    Thread.sleep(1000);
                }
            }
            if (oplogMongoDatabase != null) {
                //master/slave 架构下，日志信息在oplog.rs下面
                oplogCollection = oplogMongoDatabase.getCollection("oplog.rs");
                if (oplogCollection.count() < 1) {
                    //replica sets 架构下，日志信息在oplog.$main下面
                    oplogCollection = oplogMongoDatabase.getCollection("oplog.$main");
                }
            }
            LOGGER.info("Connect to database successfully");
        } catch (Exception e) {
            LOGGER.error("Connect to database failed", e);
            e.printStackTrace();
        }
        return mongoDatabase;
    }

    public void writeAllData(DatabaseInfo databaseInfo, TargetInfo targetInfo) {
        switch (targetInfo.getType()) {
            case ConstantSymbol.KAFKA:
                new WriteMongodbDataToKafka().writeAllData(databaseInfo, targetInfo);
                break;
            case ConstantSymbol.HBASE:
                new WriteMongodbDataToHbase().writeAllData(databaseInfo, targetInfo);
                break;
            default:
                break;
        }

    }

    public void writeAddData(DatabaseInfo databaseInfo, TargetInfo targetInfo, AddDataConfig addDataConfig) {
        switch (targetInfo.getType()) {
            case ConstantSymbol.KAFKA:
                new WriteMongodbDataToKafka().writeAddData(databaseInfo, targetInfo, addDataConfig);
                break;
            case ConstantSymbol.HBASE:
                new WriteMongodbDataToHbase().writeAddData(databaseInfo, targetInfo, addDataConfig);
                break;
            default:
                break;
        }
    }

}
