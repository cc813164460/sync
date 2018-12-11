package com.hsgene.mysql.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.kafka.model.KafkaInfo;
import com.hsgene.kafka.util.KafkaProducerSender;
import com.hsgene.model.*;
import com.hsgene.mysql.model.MysqlAddDataConfig;
import com.hsgene.mysql.model.MysqlInfo;
import com.hsgene.util.SaveFlag;
import com.hsgene.util.StringUtil;
import kafka.producer.KeyedMessage;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 10:50 2017/10/19
 * @Modified By:
 */
public class WriteMysqlDataToKafka extends WriteMysqlData {

    private final static Logger LOGGER = Logger.getLogger(WriteMysqlDataToKafka.class);
    private static String topic = "mysql_topic";
    private ResultSet columnsResultSet = null;

    @Override
    public void writeAllData(DatabaseInfo databaseInfo, TargetInfo targetInfo) {
        try {
            KafkaInfo kafkaInfo = (KafkaInfo) targetInfo.getObject();
            MysqlInfo mysqlInfo = (MysqlInfo) databaseInfo;
            SendFlag sendFlag = new SendFlag();
            sendFlag.setTargetInfo(targetInfo);
            sendFlag.setUrl(mysqlInfo.getHost() + ":" + mysqlInfo.getPort());
            sendFlag.setDatabase(mysqlInfo.getDatabase());
            sendFlag.setTable(mysqlInfo.getTables());
            sendFlag.setType(ConstantSymbol.MYSQL);
            initConnection(mysqlInfo);
            topic = kafkaInfo.getTopic();
            Connection connection = writeMysqlDds.getConnection();
            ResultSet resultSet = connection.prepareStatement("show master status").executeQuery();
            KafkaProducerSender sender = new KafkaProducerSender(kafkaInfo);
            ResultSet logsName = connection.prepareStatement("show binary logs").executeQuery();
            String currentLogName = "";
            int currentLogPos = -1;
            while (logsName.next()) {
                if (logsName.isLast()) {
                    currentLogName = logsName.getString(1);
                }
            }
            ResultSet logInfos = connection.prepareStatement("show binlog events in '" + currentLogName + "'")
                .executeQuery();
            while (logInfos.next()) {
                if (logInfos.isLast()) {
                    currentLogPos = logInfos.getInt(5);
                }
            }
            //处理是否是全数据库或者全连接
            List<String> tablenameList = getAllTableName();
            DataSendToKafka dataSendToKafka = new DataSendToKafka();
            dataSendToKafka.setAction(ConstantSymbol.ACTION_TYPE_INSERT);
            dataSendToKafka.setSource(mysqlInfo.getHost());
            ExecutorService pool = new ThreadPoolExecutor(POOL_NUM, 200, 0L, TimeUnit.MILLISECONDS, new
                LinkedBlockingQueue<Runnable>());
            List<KeyedMessage<String, String>> keyedMessageList = new ArrayList<>();
            for (int i = 0, length = tablenameList.size(); i < length; i++) {
                //表名为空时传输全部表，不为空则传输指定表
                String tablename = tablenameList.get(i);
                List<String> getTableList = new ArrayList<>();
                String getDatabase = mysqlInfo.getDatabase();
                String getHost = mysqlInfo.getHost();
                if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(tablename))) {
                    ResultSet tableInfo = connection.prepareStatement("select * from `" + tablename + "`")
                        .executeQuery();
                    ResultSetMetaData metaData = tableInfo.getMetaData();
                    while (tableInfo.next()) {
                        dataSendToKafka = new DataSendToKafka();
                        dataSendToKafka.setSource(getHost);
                        StringBuilder databaseAndTable = new StringBuilder(getDatabase);
                        databaseAndTable.append(".");
                        databaseAndTable.append(tablename);
                        dataSendToKafka.setTable(databaseAndTable.toString());
                        dataSendToKafka.setAction(ConstantSymbol.ACTION_TYPE_INSERT);
                        JSONObject json = new JSONObject();
                        for (int j = 1, lengthTemp = metaData.getColumnCount(); j <= lengthTemp; j++) {
                            String name = metaData.getColumnName(j);
                            Object value = tableInfo.getObject(j);
                            json.put(name, value);
                        }
                        dataSendToKafka.setData(json);
                        //传list提高速度
                        keyedMessageList.add(new <String, String>KeyedMessage(topic, dataSendToKafka.toString()));
                        if (keyedMessageList.size() >= EVERY_COUNT_MAX) {
                            sender.send(keyedMessageList, pool);
                            keyedMessageList = new ArrayList<>();
                        }
                    }
                }
            }
            sender.send(keyedMessageList, pool);
            //阻止新来的任务提交，对已经提交了的任务不会产生任何影响。当已经提交的任务执行完后，它会将那些闲置的线程（idleWorks）进行中断，这个过程是异步的
            pool.shutdown();
            //等待线程执行完
            while (!pool.isTerminated()) {
            }
            MysqlAddDataConfig tempAddDataConfig = new MysqlAddDataConfig();
            while (resultSet.next()) {
                currentLogName = resultSet.getString(1);
                currentLogPos = Integer.valueOf(resultSet.getString(2));
            }
            tempAddDataConfig.setEndLogPathAndName(currentLogName);
            tempAddDataConfig.setEndLogPos(currentLogPos);
            sendFlag.setAddDataConfig(tempAddDataConfig);
            sender.close();
            //写入文件保存位置
            SaveFlag.setSendFlag(sendFlag);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    @Override
    public void writeAddData(DatabaseInfo databaseInfo, TargetInfo targetInfo, AddDataConfig addDataConfig) {
        try {
            KafkaInfo kafkaInfo = (KafkaInfo) targetInfo.getObject();
            MysqlAddDataConfig mysqlAddDataConfig = (MysqlAddDataConfig) addDataConfig;
            MysqlInfo mysqlInfo = (MysqlInfo) databaseInfo;
            topic = kafkaInfo.getTopic();
            KafkaProducerSender sender = new KafkaProducerSender(kafkaInfo);
            String destination = mysqlAddDataConfig.getCanalDestination();
            String source = mysqlInfo.getHost();
            List<String> getTableList = new ArrayList<>();
            if (destination != null) {
                CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(mysqlInfo.getHost
                    (), mysqlInfo.getPort()), destination, "", "");
                int batchSize = 1000;
                try {
                    connector.connect();
                    String database = mysqlInfo.getDatabase();
                    for (int i = 0, length = getTableList.size(); i < length; i++) {
                        getTableList.set(i, database + "\\." + getTableList.get(i));
                    }
                    if (getTableList.size() < 1) {
                        connector.subscribe(database + "\\..*");
                    } else {
                        connector.subscribe(StringUtils.join(getTableList, ","));
                    }
                    connector.rollback();
                    while (true) {
                        List<KeyedMessage<String, String>> keyedMessageList = new ArrayList<>();
                        ExecutorService pool = new ThreadPoolExecutor(POOL_NUM, 200, 0L, TimeUnit.MILLISECONDS, new
                            LinkedBlockingQueue<Runnable>());
                        Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                        long batchId = message.getId();
                        try {
                            List<CanalEntry.Entry> entryList = message.getEntries();
                            int size = entryList.size();
                            if (batchId != -1 && size != 0) {
                                for (CanalEntry.Entry entry : entryList) {
                                    CanalEntry.RowChange rowChage = null;
                                    try {
                                        rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                                    } catch (Exception e) {
                                        throw new RuntimeException("ERROR ## parser of eromanga-event has an error , " +
                                                                   "data:" + entry.toString(), e);
                                    }
                                    CanalEntry.EventType eventType = rowChage.getEventType();
                                    if (eventType == CanalEntry.EventType.ALTER || eventType == CanalEntry.EventType
                                        .ERASE || eventType == CanalEntry.EventType.CREATE) {
                                        String info = "use `" + entry.getHeader().getSchemaName() + "`; " + rowChage
                                            .getSql();
                                        List<DataSendToKafka> list = getDealInfo(info, source, getTableList);
                                        for (int i = 0, length = list.size(); i < length; i++) {
                                            keyedMessageList.add(new <String, String>KeyedMessage(topic, list.get(i)
                                                .toString()));
                                        }
                                    } else {
                                        for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                                            DataSendToKafka dataSendToKafka = new DataSendToKafka();
                                            CanalEntry.Header header = entry.getHeader();
                                            String schemaName = header.getSchemaName();
                                            String tableName = header.getTableName();
                                            dataSendToKafka.setTable(schemaName + "." + tableName);
                                            dataSendToKafka.setSource(source);
                                            if (eventType == CanalEntry.EventType.DELETE) {
                                                dataSendToKafka.setAction(ConstantSymbol.ACTION_TYPE_DELETE);
                                                Map<String, JSONArray> mapBefore = getColumnAndValue(rowData
                                                    .getBeforeColumnsList());
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put(ConstantSymbol.COLUMNS, mapBefore.get(ConstantSymbol
                                                    .COLUMNS));
                                                jsonObject.put(ConstantSymbol.VALUES, mapBefore.get(ConstantSymbol
                                                    .VALUES));
                                                dataSendToKafka.setData(jsonObject);
                                            } else if (eventType == CanalEntry.EventType.INSERT) {
                                                dataSendToKafka.setAction(ConstantSymbol.ACTION_TYPE_INSERT);
                                                Map<String, JSONArray> mapAfter = getColumnAndValue(rowData
                                                    .getAfterColumnsList());
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put(ConstantSymbol.COLUMNS, mapAfter.get(ConstantSymbol
                                                    .COLUMNS));
                                                jsonObject.put(ConstantSymbol.VALUES, mapAfter.get(ConstantSymbol
                                                    .VALUES));
                                                dataSendToKafka.setData(jsonObject);
                                            } else {
                                                dataSendToKafka.setAction(ConstantSymbol.ACTION_TYPE_UPDATE);
                                                Map<String, JSONArray> mapBefore = getColumnAndValue(rowData
                                                    .getBeforeColumnsList());
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put(ConstantSymbol.WHERECOLUMNS, mapBefore.get(ConstantSymbol
                                                    .COLUMNS));
                                                jsonObject.put(ConstantSymbol.WHEREVALUES, mapBefore.get(ConstantSymbol
                                                    .VALUES));
                                                Map<String, JSONArray> mapAfter = getColumnAndValue(rowData
                                                    .getAfterColumnsList());
                                                jsonObject.put(ConstantSymbol.COLUMNS, mapAfter.get(ConstantSymbol
                                                    .COLUMNS));
                                                jsonObject.put(ConstantSymbol.VALUES, mapAfter.get(ConstantSymbol
                                                    .VALUES));
                                                dataSendToKafka.setData(jsonObject);
                                            }
                                            keyedMessageList.add(new <String, String>KeyedMessage(topic, dataSendToKafka
                                                .toString()));
                                        }
                                    }
                                    if (keyedMessageList.size() >= EVERY_COUNT_MAX) {
                                        sender.send(keyedMessageList, pool);
                                        //写入文件保存位置
                                        keyedMessageList = new ArrayList<>();
                                    }
                                    rowChage = null;
                                }
                            }
                            connector.ack(batchId); // 提交确认
                            entryList = null;
                        } catch (CanalClientException e) {
                            connector.rollback(batchId); // 处理失败, 回滚数据
                            e.printStackTrace();
                            LOGGER.error(e);
                        }
                        if (keyedMessageList.size() > 0) {
                            //最后发送
                            sender.send(keyedMessageList, pool);
                            //阻止新来的任务提交，对已经提交了的任务不会产生任何影响。当已经提交的任务执行完后，它会将那些闲置的线程（idleWorks）进行中断，这个过程是异步的
                            pool.shutdown();
                            //等待线程执行完
                            while (!pool.isTerminated()) {
                            }
                        }
                        pool = null;
                        message = null;
                        System.gc();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(e);
                } finally {
                    connector.disconnect();
                    return;
                }
            }
            initConnection(mysqlInfo);
            if (mysqlAddDataConfig.getEndLogName() == null) {
                throw new IllegalAccessException("没有配置mysql日志");
            }
            SendFlag sendFlag = new SendFlag();
            sendFlag.setTargetInfo(targetInfo);
            sendFlag.setUrl(mysqlInfo.getHost() + ":" + mysqlInfo.getPort());
            sendFlag.setDatabase(mysqlInfo.getDatabase());
            sendFlag.setTable(mysqlInfo.getTables());
            sendFlag.setType(ConstantSymbol.MYSQL);
            long lastModified = 0;
            Connection connection = writeMysqlDds.getConnection();
            while (true) {
                List<String> logNameList = new LinkedList<>();
                ResultSet logsName = connection.prepareStatement("show binary logs").executeQuery();
                while (logsName.next()) {
                    String name = logsName.getString(1);
                    if (mysqlAddDataConfig.getEndLogName() == null) {
                        logNameList.add(name);
                    } else {
                        if (mysqlAddDataConfig.getEndLogName().compareTo(name) <= 0) {
                            logNameList.add(name);
                        }
                    }
                }
                String lastLogName = logNameList.get(logNameList.size() - 1);
                String logPathAndName = mysqlAddDataConfig.getEndLogPath() + lastLogName;
                File file = new File(logPathAndName);
                if (lastModified != file.lastModified()) {
                    //上次修改时间设置为这次
                    lastModified = file.lastModified();
                    String currentLogName = mysqlAddDataConfig.getEndLogName();
                    int currentLogPos = mysqlAddDataConfig.getEndLogPos();
                    MysqlAddDataConfig tempAddDataConfig = new MysqlAddDataConfig();
                    tempAddDataConfig.setEndLogName(currentLogName);
                    tempAddDataConfig.setEndLogPos(currentLogPos);
                    List<KeyedMessage<String, String>> keyedMessageList = new ArrayList<>();
                    ExecutorService pool = new ThreadPoolExecutor(POOL_NUM, 200, 0L, TimeUnit.MILLISECONDS, new
                        LinkedBlockingQueue<Runnable>());
                    for (int i = 0, length = logNameList.size(); i < length; i++) {
                        String name = logNameList.get(i);
                        if (mysqlAddDataConfig.getEndLogName().compareTo(name) <= 0) {
                            String filename = mysqlAddDataConfig.getEndLogPath() + name;
                            StringBuilder command = new StringBuilder("mysqlbinlog --base64-output=decode-rows -v \"");
                            command.append(mysqlAddDataConfig.getEndLogPath());
                            command.append(name);
                            command.append("\"");
                            Process process = Runtime.getRuntime().exec(command.toString());
                            InputStream input = process.getInputStream();
                            Reader reader = new InputStreamReader(input);
                            BufferedReader bufferedReader = new BufferedReader(reader);
                            StringBuilder sql = new StringBuilder("show binlog events in '");
                            sql.append(name);
                            sql.append("'");
                            ResultSet logInfos = connection.prepareStatement(sql.toString()).executeQuery();
                            while (logInfos.next()) {
                                int endLogPos = logInfos.getInt(5);
                                int startLogPos = logInfos.getInt(2);
                                currentLogName = name;
                                currentLogPos = endLogPos;
                                if (startLogPos >= mysqlAddDataConfig.getEndLogPos()) {
                                    DataSendToKafka dataSendToKafka = new DataSendToKafka();
                                    dataSendToKafka.setSource(source);
                                    String info = logInfos.getString(6);
                                    //row模式的信息
                                    if (info.startsWith("table_id: ") && info.indexOf("(") != -1) {
                                        String[] strs = info.substring(info.indexOf("(") + "(".length(), info.indexOf
                                            (")")).split("\\.");
                                        String database = strs[0];
                                        String table = strs[1];
                                        //数据库名为空时传输全部数据库，不为空则传输指定数据库
                                        String getDatabase = mysqlInfo.getDatabase();
                                        if (getDatabase.length() == 0) {
                                            addAllKeyedMessagesList(filename, bufferedReader, endLogPos, mysqlInfo,
                                                keyedMessageList, connection);
                                        } else if (getDatabase.length() != 0 || database.equals(getDatabase)) {
                                            //表名为空时传输全部表，不为空则传输指定表
                                            if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList
                                                .contains(table))) {
                                                addAllKeyedMessagesList(filename, bufferedReader, endLogPos, mysqlInfo,
                                                    keyedMessageList, connection);
                                            }
                                        }
                                    }
                                    //statement模式的信息
                                    else if (info.startsWith("use")) {
                                        String database = info.split(" ")[1].replace("`", "").replace(";", "");
                                        //数据库名为空时传输全部数据库，不为空则传输指定数据库
                                        String getDatabase = mysqlInfo.getDatabase();
                                        if (getDatabase.length() == 0) {
                                            addKeyedMessagesList(info, logInfos, mysqlAddDataConfig, name, source,
                                                keyedMessageList, getTableList);
                                        } else if (getDatabase.length() != 0 || database.equals(getDatabase)) {
                                            addKeyedMessagesList(info, logInfos, mysqlAddDataConfig, name, source,
                                                keyedMessageList, getTableList);
                                        }
                                    }
                                }
                                if (keyedMessageList.size() >= EVERY_COUNT_MAX) {
                                    sender.send(keyedMessageList, pool);
                                    //写入文件保存位置
                                    tempAddDataConfig.setEndLogName(currentLogName);
                                    tempAddDataConfig.setEndLogPos(currentLogPos);
                                    sendFlag.setAddDataConfig(tempAddDataConfig);
                                    SaveFlag.setSendFlag(sendFlag);
                                    keyedMessageList = new ArrayList<>();
                                }
                            }
                            if (process != null) {
                                process.destroy();
                            }
                            if (input != null) {
                                input.close();
                            }
                            if (reader != null) {
                                reader.close();
                            }
                            if (bufferedReader != null) {
                                bufferedReader.close();
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
                    tempAddDataConfig.setEndLogName(currentLogName);
                    tempAddDataConfig.setEndLogPos(currentLogPos);
                    sendFlag.setAddDataConfig(tempAddDataConfig);
                    //写入文件保存位置
                    SaveFlag.setSendFlag(sendFlag);
                    //作为常驻进程使用，不用考虑容错
                    mysqlAddDataConfig.setEndLogPathAndName(logPathAndName);
                    mysqlAddDataConfig.setEndLogPos(currentLogPos);
                    tempAddDataConfig = null;
                    keyedMessageList = null;
                    pool = null;
                }
                logNameList = null;
                logsName = null;
                lastLogName = null;
                logPathAndName = null;
                file = null;
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    private void addAllKeyedMessagesList(String filename, BufferedReader bufferedReader, int startLogPos, MysqlInfo
        mysqlInfo, List<KeyedMessage<String, String>> keyedMessagesList, Connection connection) throws
        IllegalArgumentException {
        if (isExist(filename)) {
            List<KeyedMessage<String, String>> keyedMessagesListTemp = dealLogFileInfo(bufferedReader, startLogPos,
                mysqlInfo, connection);
            if (keyedMessagesListTemp != null) {
                keyedMessagesList.addAll(keyedMessagesListTemp);
            }
        } else {
            throw new IllegalArgumentException(filename + "日志文件不存在");
        }
    }

    private void addKeyedMessagesList(String info, ResultSet logInfos, MysqlAddDataConfig mysqlAddDataConfig, String
        name, String source, List<KeyedMessage<String, String>> keyedMessagesList, List<String>
                                          getTableList) {
        try {
            String strInfo = "";
            if (info.split(";")[1].split(" ").length > 3) {
                strInfo = info.split(";")[1].split(" ")[3];
            }
            List<DataSendToKafka> list = new ArrayList<>();
            if (!StringUtil.CONTAIN_CREATE.matcher(info.split(";")[1].substring(1, "CREATE".length() + 1)).matches
                () && !strInfo.contains("MODIFY")) {
                int pos = logInfos.getInt(2);
                if (mysqlAddDataConfig.getEndLogName().compareTo(name) == 0) {
                    if (mysqlAddDataConfig.getEndLogPos() <= pos) {
                        list = getDealInfo(info, source, getTableList);
                    }
                } else if (mysqlAddDataConfig.getEndLogName().compareTo(name) < 0) {
                    list = getDealInfo(info, source, getTableList);
                }
            }
            for (int i = 0, length = list.size(); i < length; i++) {
                keyedMessagesList.add(new <String, String>KeyedMessage(topic, list.get(i).toString()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
    }

    private List<DataSendToKafka> getDealInfo(String info, String source, List<String> getTableList) {
        try {
            //大小写识别判断
            String[] infos = info.split(";");
            String databaseName = infos[0].substring("use".length(), infos[0].length()).replace(" ", "").replace("`",
                "");
            String table;
            StringBuilder databaseAndTable = new StringBuilder();
            String action = "";
            infos[1] = infos[1].substring(1, infos[1].length());
            List<DataSendToKafka> list = new ArrayList<>();
            DataSendToKafka dataSendToKafka = new DataSendToKafka();
            dataSendToKafka.setSource(source);
            //表的操作
            if (StringUtil.CONTAIN_DROP.matcher(infos[1].substring(0, "DROP".length())).matches() ||
                StringUtil.CONTAIN_CREATE.matcher(infos[1].substring(0, "CREATE".length())).matches() ||
                StringUtil.CONTAIN_ALTER.matcher(infos[1].substring(0, "ALTER".length())).matches()) {
                String tableInfo = infos[1].substring(infos[1].toLowerCase().indexOf("table") + "table".length(),
                    infos[1].length());
                infos[1] = infos[1].replace("\n", "").replace("\t", "").replace("\r", "");
                //获取表名
                if (tableInfo.indexOf("`") != -1) {
                    table = tableInfo.substring(tableInfo.indexOf("`") + "`".length(), tableInfo.indexOf("`",
                        tableInfo.indexOf("`") + "`".length()));
                } else {
                    table = tableInfo.split(" ")[1];
                }
                //表名为空时传输全部表，不为空则传输指定表
                if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(table))) {
                    databaseAndTable.append(databaseName);
                    databaseAndTable.append(".");
                    databaseAndTable.append(table);
                    //删除和修改，新建表是插入数据，获取操作和数据
                    if (StringUtil.CONTAIN_DROP.matcher(infos[1].substring(0, "DROP".length())).matches()) {
                        action = ConstantSymbol.ACTION_TYPE_DROP;
                        dataSendToKafka.setAction(action);
                        dataSendToKafka.setTable(databaseAndTable.toString());
                        list.add(dataSendToKafka);
                    } else if (StringUtil.CONTAIN_ALTER.matcher(infos[1].substring(0, "ALTER".length())).matches()) {
                        action = ConstantSymbol.ACTION_TYPE_ALTER;
                        dataSendToKafka.setAction(action);
                        dataSendToKafka.setTable(databaseAndTable.toString());
                        Map<String, List<String>> map = getColumn(infos[1]);
                        //重命名表
                        if (StringUtil.CONTAIN_RENAME.matcher(infos[1]).matches()) {
                            dataSendToKafka.setData(StringUtils.join(map.get(ConstantSymbol.RENAMEFIELDS), ","));
                            list.add(dataSendToKafka);
                        }
                        //删除字段，增加字段要新增数据才会操作，否则不关心
                        if (StringUtil.CONTAIN_DROP_COLUMN.matcher(infos[1]).matches()) {
                            JSONObject json = new JSONObject();
                            json.put("type", ConstantSymbol.ACTION_TYPE_DELETE);
                            json.put(ConstantSymbol.FIELDS, StringUtils.join(map.get(ConstantSymbol.DROPFIELDS), ","));
                            dataSendToKafka.setTable(databaseAndTable.toString());
                            dataSendToKafka.setData(json);
                            list.add(dataSendToKafka);
                        }
                        //修改字段类型，不关心，只关心修改字段名
                        if (StringUtil.CONTAIN_CHANGE_COLUMN.matcher(infos[1]).matches()) {
                            JSONObject json = new JSONObject();
                            json.put("type", ConstantSymbol.ACTION_TYPE_RENAME);
                            json.put(ConstantSymbol.FIELDS, StringUtils.join(map.get(ConstantSymbol.FIELDS), ","));
                            json.put(ConstantSymbol.OLDFIELDS, StringUtils.join(map.get(ConstantSymbol.OLDFIELDS), ","));
                            dataSendToKafka.setTable(databaseAndTable.toString());
                            dataSendToKafka.setData(json);
                            list.add(dataSendToKafka);
                        }
                        //新增字段
                        if (StringUtil.CONTAIN_ADD_COLUMN.matcher(infos[1]).matches()) {
                            JSONObject json = new JSONObject();
                            json.put("type", ConstantSymbol.ACTION_TYPE_INSERT);
                            json.put(ConstantSymbol.FIELDS, StringUtils.join(map.get(ConstantSymbol.ADDFIELDS), ","));
                            dataSendToKafka.setData(json);
                            list.add(dataSendToKafka);
                        }
                    }
                }
            }
            //数据的操作
            else {
                getDealSQLInfo(infos[1], dataSendToKafka, databaseName, getTableList);
                list.add(dataSendToKafka);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
        return null;
    }

    private void setKeyedMessageList(JSONObject jsonObject, String table, String action, String sourceUrl,
                                     DataSendToKafka dataSendToKafka, List<KeyedMessage<String, String>>
                                         keyedMessageList) {
        dataSendToKafka.setData(jsonObject);
        dataSendToKafka.setTable(table.replace("`", ""));
        dataSendToKafka.setAction(action);
        dataSendToKafka.setSource(sourceUrl);
        keyedMessageList.add(new <String, String>KeyedMessage(topic, dataSendToKafka.toString()));
    }

    private void jugeSetKeyedMessageList(JSONObject jsonObject, List<String> wheresList, List<String> valuesList,
                                         String table, String action, String sourceUrl, DataSendToKafka
                                             dataSendToKafka, List<KeyedMessage<String, String>> keyedMessageList) {
        if (jsonObject.size() > 0) {
            if (valuesList.size() > 0) {
                jsonObject.put("values", valuesList);
            }
            if (wheresList.size() > 0) {
                jsonObject.put("wherevalues", wheresList);
            }
            setKeyedMessageList(jsonObject, table, action, sourceUrl, dataSendToKafka, keyedMessageList);
        }
    }

    //处理row下面的语句
    private List<KeyedMessage<String, String>> dealLogFileInfo(BufferedReader bufferedReader, int logPos, MysqlInfo
        mysqlInfo, Connection connection) {
        try {
            String line;
            String table = "";
            String action = "";
            JSONObject jsonObject = new JSONObject();
            List<String> columnsList = new LinkedList<>();
            List<String> valuesList = new LinkedList<>();
            List<String> wheresList = new LinkedList<>();
            DataSendToKafka dataSendToKafka = new DataSendToKafka();
            List<KeyedMessage<String, String>> keyedMessageList = new LinkedList<>();
            boolean posFlag = false;
            String type = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("# at " + logPos) || logPos == 0) {
                    posFlag = true;
                }
                if (line.startsWith("# at ")) {
                    int index = Integer.valueOf(line.substring("# at ".length(), line.length()));
                    if (index > logPos) {
                        break;
                    }
                }
                if (posFlag && line.startsWith("### ")) {
                    if (line.startsWith("### INSERT") || line.startsWith("### UPDATE") || line.startsWith("### " +
                                                                                                          "DELETE")) {
                        if (jsonObject.size() < 1) {
                            jugeSetKeyedMessageList(jsonObject, wheresList, valuesList, table, action, mysqlInfo
                                .getHost(), dataSendToKafka, keyedMessageList);
                            dataSendToKafka = new DataSendToKafka();
                            jsonObject = new JSONObject();
                            columnsList = new LinkedList<>();
                            valuesList = new LinkedList<>();
                            wheresList = new LinkedList<>();
                        }
                        if (line.startsWith("### INSERT")) {
                            table = line.split(" ")[3];
                            action = ConstantSymbol.ACTION_TYPE_INSERT;
                        } else if (line.startsWith("### UPDATE")) {
                            table = line.split(" ")[2];
                            action = ConstantSymbol.ACTION_TYPE_UPDATE;
                        } else if (line.startsWith("### DELETE")) {
                            table = line.split(" ")[3];
                            action = ConstantSymbol.ACTION_TYPE_DELETE;
                        }
                    } else {
                        //表名不为空时获取列名
                        if (StringUtil.isNotNullAndNotBlank(table)) {
                            columnsResultSet = connection.prepareStatement("SHOW COLUMNS FROM " + table).executeQuery();
                        }
                        if (jsonObject.size() < 1) {
                            while (columnsResultSet.next()) {
                                columnsList.add(columnsResultSet.getString(1));
                            }
                            jsonObject.put("names", columnsList);
                            if (!ConstantSymbol.ACTION_TYPE_INSERT.equals(action)) {
                                jsonObject.put("wherenames", columnsList);
                            }
                        }
                        if (line.startsWith("###   ")) {
                            if ("set".equals(type)) {
                                setDataList(line, valuesList);
                            } else {
                                setDataList(line, wheresList);
                            }
                        } else {
                            if (line.startsWith("### WHERE")) {
                                type = "where";
                            } else {
                                type = "set";
                            }
                        }
                    }
                }
            }
            jugeSetKeyedMessageList(jsonObject, wheresList, valuesList, table, action, mysqlInfo.getHost(),
                dataSendToKafka, keyedMessageList);
            return keyedMessageList;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e);
        }
        return null;
    }

    private boolean isExist(String filename) {
        return new File(filename).exists();
    }
}
