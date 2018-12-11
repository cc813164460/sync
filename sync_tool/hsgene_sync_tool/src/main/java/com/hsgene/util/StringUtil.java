package com.hsgene.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.client.impl.ClusterCanalConnector;
import com.alibaba.otter.canal.client.impl.SimpleCanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.hbase.model.HbaseInfo;
import com.hsgene.hbase.model.TableInfo;
import com.hsgene.model.AddDataConfig;
import com.hsgene.model.DatabaseInfo;
import com.hsgene.mongodb.model.MongodbAddDataConfig;
import com.hsgene.mongodb.model.MongodbInfo;
import com.hsgene.mysql.model.MysqlInfo;
import com.hsgene.mysql.util.WriteMysqlData;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoQueryException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 10:44 2017/8/10
 * @Modified By:
 */
public class StringUtil {

    private final static Logger LOGGER = Logger.getLogger(StringUtil.class);

    private static Map<String, Long> exampleTimeout = new HashMap<>();

    public static final Pattern CONTAIN_RENAME = Pattern.compile("^.*((?i)rename).*$");
    public static final Pattern CONTAIN_DROP_COLUMN = Pattern.compile("^.*((?i)drop column).*$");
    public static final Pattern CONTAIN_CHANGE_COLUMN = Pattern.compile("^.*((?i)change column).*$");
    public static final Pattern CONTAIN_ADD_COLUMN = Pattern.compile("^.*((?i)add column).*$");

    public static final Pattern CONTAIN_CREATE = Pattern.compile("^.*((?i)create).*$");
    public static final Pattern CONTAIN_DROP = Pattern.compile("^.*((?i)drop).*$");
    public static final Pattern CONTAIN_ALTER = Pattern.compile("^.*((?i)alter).*$");
    public static final Pattern EXP_HOST = Pattern.compile("(?<=//)\\d{1,3}\\.\\d{1,3}.\\d{1,3}\\.\\d{1,3}:\\d+(?=/)");

    public static final Pattern BRACKET_VALUE = Pattern.compile("\\[[0-9]+\\]");
    public static final Pattern DATETIME = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.0$");

    static final String MONGDBINFOCLASSNAME = "com.hsgene.mongodb.model.MongodbInfo";
    static final String MYSQLINFOCLASSNAME = "com.hsgene.mysql.model.MysqlInfo";

    static final String hbaseTable = "case";
    static final String relevanceID = "ref_id";
    static final String objectIdColumn = "objectId";
    static final String hbaseMainColumn = "objectId_timestamp";
    static final String mongoMainColumn = "_id.$oid";

    /**
     * @return boolean
     * @throws
     * @param: object 对象
     * @Description: 判断对象不为空且转换为string格式不为blank
     * @Date: 16:49 2017/8/24
     */
    public static String getString(Object object) {
        if (StringUtil.isNullOrBlank(object)) {
            return "";
        }
        return object.toString();
    }

    /**
     * @return boolean
     * @throws
     * @param: object 对象
     * @Description: 判断对象不为空且转换为string格式不为blank
     * @Date: 16:49 2017/8/24
     */
    public static boolean isNotNullAndNotBlank(Object object) {
        if (object != null && StringUtils.isNotBlank(object.toString())) {
            return true;
        }
        return false;
    }

    /**
     * @return boolean
     * @throws
     * @param: object 对象
     * @Description: 判断对象为空或者转换为string格式为blank
     * @Date: 16:50 2017/8/24
     */
    public static boolean isNullOrBlank(Object object) {
        return !isNotNullAndNotBlank(object);
    }

    /**
     * @return java.lang.String
     * @throws
     * @param: str
     * @param: pre
     * @Description: 根据前缀获取值
     * @Date: 11:09 2017/10/30
     */
    public static String getValueByPre(String str, String pre) {
        return str.substring(pre.length(), str.length());
    }

    /**
     * @return void
     * @throws
     * @param: start 开始时间
     * @param: count 数量
     * @Description: 计算传输速度
     * @Date: 14:51 2017/10/30
     */
    public static void printSpeed(long start, long count, String table, Logger LOGGER) {
        float spendTime = (float) (System.currentTimeMillis() - start) / 1000;
        float speed = count / spendTime;
        String info = "\n表名：" + table + ";\n数量：" + count + ";\n" + "时间：" + spendTime + "秒;\n每秒：" + String.format
            ("%.2f", speed) + ";\n";
        System.out.println(info);
        LOGGER.info(info);
    }

    /**
     * @return boolean
     * @throws
     * @param: str 字符串
     * @Description: 判断是否是整数
     * @Date: 16:02 2017/9/19
     */
    public static boolean isInteger(String str) {
        return patternMacther(str, "^[-+]?[\\d]*$");
    }

    /**
     * @return boolean
     * @throws
     * @param: str 字符串
     * @param: reg 正则表达式
     * @Description: 字符串匹配正则表达式
     * @Date: 16:17 2017/9/19
     */
    public static boolean patternMacther(String str, String reg) {
        if (isNotNullAndNotBlank(str)) {
            return Pattern.compile(reg).matcher(str).matches();
        }
        return false;
    }

    /**
     * @return java.lang.StringBuffer
     * @throws
     * @param: fileInputStream
     * @Description: 将FileInputStream转换为StringBuffer
     * @Date: 9:38 2017/11/15
     */
    public static StringBuffer fileInputStreamToStringBuffer(FileInputStream fileInputStream) {
        try {
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = fileInputStream.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            return out;
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static String replaceBracketValue(String str) {
        return str.replaceAll("\\[[0-9]+\\]", "");
    }

    public static String replaceEscapeCharacter(String value) {
        if (value != null) {
            if (value.indexOf("\\") != -1) {
                value = value.replace("\\", "\\\\");
            }
            if (value.indexOf("\'") != -1) {
                value = value.replace("\'", "\\'");
            }
        }
        return value;
    }

    public static boolean addJuge(CanalEntry.Entry entry, String tableName) {
        return entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry
            .EntryType.TRANSACTIONEND || tableName.length() < 1;
    }

    public static boolean isIUD(CanalEntry.EventType eventType) {
        return eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE ||
               eventType == CanalEntry.EventType.DELETE;
    }

    public static String getCreateColumnInfo(Collection collection, String pk, String columnFamily) {
        StringBuilder sb = new StringBuilder();
        for (Object column : collection) {
            if (column.equals(pk)) {
                sb.append("\"" + column + "\" varchar primary key,");
            } else {
                sb.append("\"" + columnFamily + "\".\"" + column + "\" varchar,");
            }
        }
        if (sb.length() > 0) {
            return sb.toString().substring(0, sb.toString().length() - 1);
        } else {
            return sb.toString();
        }
    }

    public static Set<String> getTableSet(String str) {
        String[] strs = str.substring(str.lastIndexOf(" from ") + " from ".length(), str.length()).split(",");
        if (str.indexOf(" where ") != -1) {
            strs = str.substring(str.lastIndexOf(" from ") + " from ".length(), str.indexOf(" where")).split(",");
        }
        Set<String> tableSet = new LinkedHashSet<>();
        String table = "";
        for (String ss : strs) {
            if (!ss.contains("(")) {
                if (ss.contains(" ")) {
                    table = ss.substring(0, ss.indexOf(" ")).replace(" ", "");
                } else {
                    table = ss.substring(0, ss.length()).replace(" ", "");
                }
                if (table.length() > 0) {
                    tableSet.add(table);
                }
            }
        }
        if (str.contains(" where ")) {
            if (str.contains(" left join ")) {
                strs = str.split("where")[0].split(" left join ");
            } else if (str.contains(" inner join ")) {
                strs = str.split("where")[0].split(" inner join ");
            }
        } else {
            if (str.contains(" left join ")) {
                strs = str.split(" left join ");
            } else if (str.contains(" inner join ")) {
                strs = str.split(" inner join ");
            }
        }
        int count = 0;
        for (String ss : strs) {
            if (count > 0) {
                if (!ss.contains("(")) {
                    table = ss.substring(0, ss.indexOf(" ")).replace(" ", "");
                    if (table.length() > 0) {
                        tableSet.add(table);
                    }
                }
            }
            count++;
        }
        return tableSet;
    }

    public static String getAlia(String sql, String tableName) {
        String table = " " + tableName + " ";
        String aliaTemp = sql.substring(sql.indexOf(table) + table.length());
        String alia;
        if (!aliaTemp.contains(" ")) {
            alia = aliaTemp;
        } else {
            alia = aliaTemp.substring(0, aliaTemp.indexOf(" "));
        }
        return alia;
    }

    public static String getAlia(String sql) {
        String aliaTemp = "";
        if (sql.indexOf("where") != -1) {
            aliaTemp = sql.substring(sql.indexOf(" from ") + " from ".length(), sql.indexOf(" where "));
        } else {
            aliaTemp = sql.substring(sql.indexOf(" from ") + " from ".length(), sql.length());
        }
        return aliaTemp.split(" ")[1];
    }

    public static JSONArray getMysqlColumn(String[] columns, String alia) {
        JSONArray columnArray = new JSONArray();
        String str = alia + ".";
        for (String column : columns) {
            int indexOf = column.indexOf(str);
            if (column.startsWith(str)) {
                try {
                    columnArray.add(column.substring(indexOf + str.length(), column.indexOf(" ")));
                } catch (Exception e) {
                    LOGGER.error(column, e);
                }
            }
        }
        return columnArray;
    }

    public static JSONArray getHbaseColumn(String[] columns, String alia) {
        JSONArray columnArray = new JSONArray();
        String str = alia + ".";
        for (String column : columns) {
            if (column.startsWith(str)) {
                try {
                    columnArray.add(column.split(" as ")[1].replace(" ", ""));
                } catch (Exception e) {
                    LOGGER.error(column, e);
                }
            }
        }
        return columnArray;
    }

    public static String getHbaseIDValue(JSONArray columnArray, JSONArray valueArray, String IDColumn) {
        String IDValue = "";
        int IDIndexOf = columnArray.indexOf(IDColumn);
        if (IDIndexOf != -1) {
            IDValue = StringUtil.dealValue(valueArray.getString(IDIndexOf));
        }
        return IDValue;
    }

    public static void resultSetUpsertInto(Connection mysqlConnection, Connection hbaseConnection, String selectSQL,
                                           String table, String hbaseMainColumn) {
        try {
            ResultSet resultSet = mysqlConnection.prepareStatement(selectSQL).executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            JSONArray columnArray = new JSONArray();
            int columnCount = metaData.getColumnCount();
            for (int j = 1; j <= columnCount; j++) {
                columnArray.add(metaData.getColumnLabel(j));
            }
            while (resultSet.next()) {
                JSONArray valueArray = new JSONArray();
                for (int i = 1; i <= columnArray.size(); i++) {
                    valueArray.add(StringUtil.dealValue(resultSet.getString(i)));
                }
                if (StringUtils.isBlank(hbaseMainColumn)) {
                    upsertInto(table, columnArray, valueArray, hbaseConnection);
                } else {
                    String hbaseMainValue = valueArray.getString(0);
                    String hbaseSQL = "select count(*) from \"" + table + "\" where \"" + hbaseMainColumn + "\" = \'"
                                      + hbaseMainValue + "\'";
                    ResultSet rs = hbaseConnection.prepareStatement(hbaseSQL).executeQuery();
                    int num = 1;
                    while (rs.next()) {
                        num = rs.getInt(1);
                    }
                    if (num < 1) {
                        upsertInto(table, columnArray, valueArray, hbaseConnection);
                    }
                }
            }
            resultSet = null;
            metaData = null;
            columnArray = null;
        } catch (Exception e) {
            LOGGER.error(selectSQL, e);
        }
    }

    public static void resultSetUpsertInto(Connection mysqlConnection, Connection hbaseConnection, String selectSQL,
                                           String table) {
        resultSetUpsertInto(mysqlConnection, hbaseConnection, selectSQL, table, null);
    }

    public static String dealValue(Object obj) {
        if (obj == null) {
            return "";
        } else {
            String str = obj.toString();
            str = StringUtil.replaceEscapeCharacter(str);
            return StringUtil.formatDatetime(str);
        }
    }

    public static String dealFormatDateTime(Object obj) {
        if (obj == null) {
            return "";
        } else {
            String str = obj.toString();
            return StringUtil.formatDatetime(str);
        }
    }

    public static void upsertInto(String table, JSONArray columnArray, JSONArray valueArray, Connection
        hbaseConnection) throws Exception {
        StringBuilder upsertSQL = new StringBuilder("upsert into \"" + table + "\"");
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder(" values(");
        for (int i = 0; i < columnArray.size(); i++) {
            values.append("\'" + StringUtil.replaceEscapeCharacter(valueArray.getString(i)) + "\',");
            columns.append("\"" + columnArray.get(i) + "\",");
        }
        //去掉逗号
        columns = new StringBuilder(columns.substring(0, columns.length() - 1));
        values = new StringBuilder(values.substring(0, values.length() - 1));
        columns.append(")");
        values.append(")");
        upsertSQL.append(columns).append(values);
        StringUtil.executeUpdate(hbaseConnection, upsertSQL.toString());
        upsertSQL = null;
        columns = null;
        values = null;
    }

    public static void mysqlSplitInsert(int indexOf, JSONArray valueArray, String getSql, String mysqlID, Connection
        connection, Connection hbaseConnection, String table) {
        if (indexOf != -1) {
            String value = valueArray.getString(indexOf);
            value = StringUtil.dealValue(value);
            String querySql = getSql + " and (a." + mysqlID + "=\'" + value + "\' or b." + mysqlID + "=\'" + value +
                              "\')";
            StringUtil.resultSetUpsertInto(connection, hbaseConnection, querySql, table);
        }
    }

    public static void mysqlConcatInsert(String getSql, String alia, List<String> columns, List<String> values,
                                         Connection connection, Connection hbaseConnection, String table) {
        StringBuilder querySql = new StringBuilder(getSql);
        for (int i = 0; i < columns.size(); i++) {
            querySql.append(" and " + alia + "." + columns.get(i) + " = \'" + values.get(i) + "\'");
        }
        StringUtil.resultSetUpsertInto(connection, hbaseConnection, querySql.toString(), table);
    }

    public static void mysqlConcatDelete(List<String> columns, List<String> values, Connection hbaseConnection,
                                         String table) {
        StringBuilder deleteSQL = new StringBuilder("delete from \"" + table + "\" where");
        for (int i = 0; i < columns.size(); i++) {
            deleteSQL.append(" \"" + columns.get(i) + "\"=\'" + values.get(i) + "\'");
        }
        if (!deleteSQL.toString().endsWith("where")) {
            StringUtil.executeUpdate(hbaseConnection, deleteSQL.toString());
        }
    }

    public static List<String> getHbaseWhereColumnList(List<String> whereColumnList, JSONArray mysqlColumnJSONArray,
                                                       JSONArray hbaseColumnJSONArray) {
        List<String> hbaseWhereList = new ArrayList<>();
        for (int i = 0; i < whereColumnList.size(); i++) {
            String tempWhereColumn = whereColumnList.get(i);
            int indexOf = mysqlColumnJSONArray.indexOf(tempWhereColumn);
            if (indexOf != -1) {
                hbaseWhereList.add(hbaseColumnJSONArray.getString(indexOf));
            }
        }
        return hbaseWhereList;
    }

    public static void mysqlAddDeleteHbase(int indexOf, JSONArray valueArray, String table, Connection
        hbaseConnection) {
        if (indexOf != -1) {
            try {
                String value = valueArray.getString(indexOf);
                value = StringUtil.dealValue(value);
                String sql = "delete from \"" + table + "\" where \"parent_id\"=\'" + value + "\' or \"child_id\"=\'" +
                             value + "\'";
                StringUtil.executeUpdate(hbaseConnection, sql);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    public static String getOnMainMySQLID(String alia, String fromBack, String mainAlia) {
        String aliaOn = " " + alia + " on ";
        String aliaOnBack = fromBack.substring(fromBack.indexOf
            (aliaOn) + aliaOn.length(), fromBack.length());
        String mainAliaOnIDStart = aliaOnBack.substring(aliaOnBack.indexOf(mainAlia) + mainAlia.length() + 1,
            aliaOnBack.length());
        return mainAliaOnIDStart.substring(0, mainAliaOnIDStart.indexOf(" "));
    }

    public static String formatJSON(String str) {
        Gson gs = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(str);
        return gs.toJson(je);
    }

    public static void executeUpdate(Connection connection, String sql) {
        try {
            if(!sql.endsWith(" is null")) {
                connection.prepareStatement(sql).executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            LOGGER.error(sql, e);
        }
    }

    public static String formatDatetime(String dateTime) {
        if (StringUtil.DATETIME.matcher(dateTime).matches()) {
            return dateTime.split("\\.")[0];
        } else {
            return dateTime;
        }
    }

    public static JSONObject splitTransferedTo(JSONObject jsonObject) {
        JSONObject retJSONObject = new JSONObject();
        Set<String> keySet = jsonObject.keySet();
        List<String> keyList = new LinkedList<>();
        keyList.addAll(keySet);
        //排序将同一个对象的放在前后，保证对应关系
        Collections.sort(keyList);
        for (String key : keyList) {
            String value = jsonObject.getString(key);
            if (StringUtil.isMongoValueNotEmpty(value) && key.startsWith(ConstantSymbol.TRANSFEREDTO)) {
                int siteNum = Integer.valueOf(key.substring(key.lastIndexOf("[") + "[".length(), key.length() - 1));
                String newKey = key.substring(0, key.lastIndexOf("["));
                if (0 == siteNum) {
                    newKey += ".site1";
                } else {
                    newKey += ".site2";
                    if (null != retJSONObject.get(newKey)) {
                        value = retJSONObject.get(newKey) + "," + value;
                    }
                }
                retJSONObject.put(newKey, value);
                newKey = null;
            } else if (StringUtil.isMongoValueNotEmpty(value)) {
                retJSONObject.put(key, value);
            }
            key = null;
            value = null;
        }
        keySet = null;
        keyList = null;
        return retJSONObject;
    }

    public static JSONObject combineColumn(JSONObject jsonObject, JSONArray combineColumnArray) {
        JSONObject retJSONObject = new JSONObject();
        String split = ",";
        List<String> keyList = new LinkedList<>();
        keyList.addAll(jsonObject.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            String value = jsonObject.getString(key);
            String rBKey = StringUtil.replaceBracketValue(key);
            //不拆分的数组
            if (-1 != combineColumnArray.indexOf(rBKey)) {
                //最内层是数组的不拆分
                if (key.endsWith("]")) {
                    String subKey = key.substring(0, key.lastIndexOf("["));
                    String retValue = retJSONObject.getString(subKey);
                    //不为空的就追加
                    if (null != retValue) {
                        retJSONObject.put(subKey, retValue + split + value);
                    } else {
                        retJSONObject.put(subKey, value);
                    }
                    subKey = null;
                    retValue = null;
                }
            } else {
                retJSONObject.put(key, value);
            }
            key = null;
            value = null;
            rBKey = null;
        }
        split = null;
        keyList = null;
        return retJSONObject;
    }

    /**
     * 补全同级目录数据
     *
     * @param jsonObject
     * @param mongoColumnArray
     * @return
     */
    public static JSONObject completeData(JSONObject jsonObject, JSONArray mongoColumnArray) {
        JSONObject retJSONObject = new JSONObject();
        Set<String> keySet = jsonObject.keySet();
        Set<String> inKeySet = jsonObject.keySet();
        List<String> keyList = new ArrayList<>();
        keyList.addAll(keySet);
        Collections.sort(keyList);
        for (String key : keySet) {
            String value = jsonObject.getString(key);
            retJSONObject.put(key, value);
            boolean keyEndsWith = key.endsWith("]");
            String rBKey = StringUtil.replaceBracketValue(key);
            String subTKey = "";
            String subKeyFront = "";
            boolean rBKeyIndex = rBKey.indexOf(".") != -1;
            if (rBKeyIndex) {
                subTKey = rBKey.substring(0, rBKey.lastIndexOf("."));
                subKeyFront = key.substring(0, key.lastIndexOf("."));
            }
            if (-1 != mongoColumnArray.indexOf(rBKey)) {
                if (!keyEndsWith) {
                    for (String inKey : inKeySet) {
                        String subRBKey = StringUtil.getSubPointKey(rBKey);
                        String rBInKey = StringUtil.replaceBracketValue(inKey);
                        String subRBInKey = StringUtil.getSubPointKey(rBInKey);
                        //最外层的不用补全
                        if (subRBInKey.length() == 0) {
                            continue;
                        }
                        boolean rBInKeyIndex = rBInKey.indexOf(".") != -1;
                        //不是同级，没有上下级关系(这种判断同一个)
                        //判断是否为同级且都为数组或者都为字符串
                        if (rBKeyIndex && rBInKeyIndex) {
                            String subTInKey = rBInKey.substring(0, rBInKey.lastIndexOf("."));
                            int inKeyIndexOf = inKey.lastIndexOf(".");
                            String backTKey = inKey.substring(inKeyIndexOf + ".".length(), inKey.length());
                            String subKeyBack = "";
                            if (keyEndsWith) {
                                subKeyBack = key.substring(key.lastIndexOf("["), key.length());
                            }
                            //判断是否为同一级
                            if (subTKey.equals(subTInKey)) {
                                boolean inKeyEndsWith = inKey.endsWith("]");
                                boolean isBothArray = keyEndsWith && inKeyEndsWith;
                                boolean isBothNotArray = !keyEndsWith && !inKeyEndsWith;
                                if (isBothArray || isBothNotArray) {
                                    String completeKey = subKeyFront + "." + backTKey + subKeyBack;
                                    //重复的completeKey就不添加
                                    if (!keyList.contains(completeKey)) {
                                        retJSONObject.put(completeKey, "");
                                        keyList.add(completeKey);
                                    }
                                    completeKey = null;
                                }
                            }
                            subTInKey = null;
                            backTKey = null;
                            subKeyBack = null;
                        }
                        if (rBInKey.startsWith("medical.chrt.medicinePlan")) {
                            int indexOf = inKey.indexOf(".medicinePlan");
                            String mc = inKey.substring(0, indexOf);
                            int planBackIndex = indexOf + 13;
                            String mIndex = inKey.substring(planBackIndex, inKey.indexOf(".", planBackIndex));
                            String completeKey = mc + ".opportunity" + mIndex;
                            if (!keyList.contains(completeKey)) {
                                retJSONObject.put(completeKey, "");
                                keyList.add(completeKey);
                            }
                        }
                        if (keyEndsWith) {
                            continue;
                        }
                        boolean flag1 = !subRBInKey.equals(subRBKey);
                        if (!flag1) {
                            continue;
                        }
                        boolean flag2 = subRBInKey.startsWith(subRBKey);
                        boolean flag3 = subRBKey.length() == 0;
                        boolean flag2Or3 = flag2 || flag3;
                        if (!flag2Or3) {
                            continue;
                        }
                        boolean flag = flag1 && flag2Or3;
                        //判断inKey是否为key的内层
                        if (flag) {
                            String backKey = subRBInKey.substring(subRBKey.length(), subRBInKey.length());
                            int index = inKey.indexOf(backKey);
                            if (index != -1) {
                                backKey = inKey.substring(index, inKey.length());
                                backKey = backKey.replaceAll("\\[[0-9]+\\]", "[0]");
                                String subKey = StringUtil.getSubKey(key, "]");
                                String completeKey = subKey + backKey;
                                String rBCompleteKey = StringUtil.replaceBracketValue(completeKey);
                                if (mongoColumnArray.contains(rBCompleteKey)) {
                                    if (!keyList.contains(completeKey)) {
                                        retJSONObject.put(completeKey, "");
                                        keyList.add(completeKey);
                                    }
                                }
                                subKey = null;
                                completeKey = null;
                                rBCompleteKey = null;
                            }
                            backKey = null;
                        }
                        rBInKey = null;
                        subRBKey = null;
                        subRBInKey = null;
                        inKey = null;
                    }
                }
            }
            subTKey = null;
            subKeyFront = null;
            value = null;
            rBKey = null;
            key = null;
        }
        keySet = null;
        inKeySet = null;
        keyList = null;
        return retJSONObject;
    }

    public static String getAbsolutePath(String properties) {
        String path = StringUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String absolutePath;
        if (path.endsWith("/")) {
            absolutePath = path;
        } else {
            absolutePath = path.substring(0, path.lastIndexOf("/") + 1);
        }
        absolutePath += properties;
        return absolutePath;
    }

    public static JSONObject getJSONObject(String properties) {
        try {
            String propertiesPath = StringUtil.getAbsolutePath(properties);
            FileInputStream fileInputStream = new FileInputStream(propertiesPath);
            StringBuffer stringBuffer = StringUtil.fileInputStreamToStringBuffer(fileInputStream);
            fileInputStream.close();
            return JSONObject.parseObject(stringBuffer.toString());
        } catch (Exception e) {
            LOGGER.error(properties, e);
        }
        return null;
    }

    public static String getSubKey(String key, String subStr) {
        String rootKey = "";
        int bracketCount = key.length() - key.replaceAll("]", "").length();
        if (bracketCount > 0) {
            if (subStr.equals("]")) {
                return key.substring(0, key.lastIndexOf(subStr)) + subStr;
            } else {
                return key.substring(0, key.lastIndexOf(subStr));
            }
        } else {
            return rootKey;
        }
    }

    public static String getSubPointKey(String key) {
        int lastIndex = key.lastIndexOf(".");
        if (lastIndex != -1) {
            return key.substring(0, lastIndex);
        }
        return "";
    }

    public static String getAction(Object object) {
        String action = "";
        if ("i".equals(object)) {
            action = ConstantSymbol.ACTION_TYPE_INSERT;
        } else if ("u".equals(object)) {
            action = ConstantSymbol.ACTION_TYPE_UPDATE;
        } else if ("d".equals(object)) {
            action = ConstantSymbol.ACTION_TYPE_DELETE;
        }
        return action;
    }

    public static void mongoAddMysqlAction(DruidDataSource hDds, List<CanalEntry.Entry> entryList, Set<String>
        tempSet, MongoCollection mongoCollection, JSONArray mysqlColumnJSONArray, JSONArray
                                               hbaseMysqlColumnJSONArray, JSONArray mongoColumnJSONArray, JSONArray
                                               columnJSONArray, JSONArray hbaseColumnJSONArray, String destination)
        throws Exception {
        Connection hbaseConnection = hDds.getConnection();
        for (CanalEntry.Entry entry : entryList) {
            CanalEntry.Header header = entry.getHeader();
            long binlogTime = header.getExecuteTime();
            String tableName = header.getTableName();
            //表名为空则进入下一次循环
            if (StringUtil.addJuge(entry, tableName)) {
                StringUtil.outToFile(destination + "-binlog", 0);
                continue;
            }
            CanalEntry.RowChange rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            CanalEntry.EventType eventType = rowChage.getEventType();
            if (StringUtil.isIUD(eventType)) {
                for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                    //表主id
                    if (tempSet.contains(tableName)) {
                        String hbaseMainValue = "";
                        Map<String, JSONArray> mapBefore = WriteMysqlData.getColumnAndValue(rowData
                            .getBeforeColumnsList());
                        Map<String, JSONArray> mapAfter = WriteMysqlData.getColumnAndValue(rowData
                            .getAfterColumnsList());
                        JSONArray columnBeforeArray = mapBefore.get(ConstantSymbol.COLUMNS);
                        JSONArray columnAfterArray = mapAfter.get(ConstantSymbol.COLUMNS);
                        JSONArray valueBeforeArray = mapBefore.get(ConstantSymbol.VALUES);
                        JSONArray valueAfterArray = mapAfter.get(ConstantSymbol.VALUES);
                        int indexOf = columnAfterArray.indexOf(relevanceID);
                        //插入和修改，都是upsert into
                        if (eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE) {
                            String mongodbMainValue = "";
                            //获取存入hbase中的主键的值
                            if (indexOf != -1) {
                                if (eventType == CanalEntry.EventType.UPDATE) {
                                    hbaseMainValue = valueBeforeArray.getString(indexOf);
                                } else {
                                    hbaseMainValue = valueAfterArray.getString(indexOf);
                                }
                                //获取mysql和mongodb的关联id，查询mongodb中的数据
                                mongodbMainValue = valueAfterArray.getString(indexOf);
                            }
                            List<String> hbaseMainValueList = new ArrayList<>();
                            if (hbaseMainValue.length() > 0) {
                                String selectHbase = "select \"" + hbaseMainColumn + "\" from \"" + hbaseTable + "\" " +
                                                     "where \"" + hbaseMainColumn + "\" " + "like \'" +
                                                     hbaseMainValue + ConstantSymbol.SPLIT + "%\'";
                                ResultSet resultSet = hbaseConnection.prepareStatement(selectHbase).executeQuery();
                                //查询所有的rowkey，然后更新，加入list
                                while (resultSet.next()) {
                                    hbaseMainValueList.add(resultSet.getString(1));
                                }
                                resultSet.close();
                            }
                            int hMVLSize = hbaseMainValueList.size();
                            //组装mongodb数据
                            Bson bson = new BasicDBObject("_id", new ObjectId(mongodbMainValue));
                            long mongoCount = mongoCollection.count(bson);
                            StringBuilder sbColumnMysql = new StringBuilder();
                            StringBuilder sbValueMysql = new StringBuilder();
                            //组装mysql数据
                            for (int i = 0; i < columnAfterArray.size(); i++) {
                                String column = columnAfterArray.getString(i);
                                int index = mysqlColumnJSONArray.indexOf(column);
                                if (index != -1) {
                                    String hbaseColumn = "\"" + hbaseMysqlColumnJSONArray.getString(index) + "\",";
                                    //去除重复的列
                                    if (sbColumnMysql.indexOf(hbaseColumn) == -1) {
                                        if (hbaseColumn.equals("\"" + hbaseMainColumn + "\",")) {
                                            hbaseColumn = "\"" + objectIdColumn + "\",";
                                        }
                                        sbColumnMysql.append(hbaseColumn);
                                        String value = StringUtil.dealValue(valueAfterArray.getString(i));
                                        if (hbaseColumn.equals("\"dcw_id\",") && value.length() == 0) {
                                            LOGGER.error("mysql中dcw_id为空" + valueAfterArray);
                                        }
                                        if (hbaseColumn.equals("\"objectId\",") && value.length() == 0) {
                                            LOGGER.warn("mysql中objectId为空" + valueAfterArray);
                                        }
                                        sbValueMysql.append("\'" + value + "\',");
                                    }
                                }
                            }
                            if (sbColumnMysql.indexOf("\"dcw_id\",") == -1) {
                                LOGGER.error("mysql没有dcw_id" + valueAfterArray);
                            }
                            if (sbColumnMysql.indexOf("\"objectId\",") == -1) {
                                LOGGER.warn("mysql没有objectId" + valueAfterArray);
                            }
                            //查询mongo中是否有数据
                            if (mongoCount > 0) {
                                FindIterable<Document> findIterable = mongoCollection.find(bson);
                                for (Document document : findIterable) {
                                    String jsonStr = document.toJson();
                                    List<JSONObject> splitList = StringUtil.splitCaseMongoData(jsonStr,
                                        mongoColumnJSONArray);
                                    int splitListSize = splitList.size();
                                    //删除没有同步的数据，有可能多数据的问题
                                    for (int i = 0; i < hMVLSize; i++) {
                                        String value = hbaseMainValueList.get(i);
                                        String deleteSQL = "delete from \"" + hbaseTable + "\" where \"" +
                                                           hbaseMainColumn + "\" = \'" + value + "\'";
                                        StringUtil.executeUpdate(hbaseConnection, deleteSQL);
                                    }
                                    for (int i = 0; i < splitListSize; i++) {
                                        StringBuilder sbColumn = new StringBuilder(sbColumnMysql);
                                        StringBuilder sbValue = new StringBuilder(sbValueMysql);
                                        JSONObject jsonMulti = splitList.get(i);
                                        //将mongo拆分过后的数据加入字段中
                                        Set<String> keySet = jsonMulti.keySet();
                                        for (String key : keySet) {
                                            int index = columnJSONArray.indexOf(key);
                                            if (jsonMulti.get(key) != null && index != -1) {
                                                String column = "\"" + hbaseColumnJSONArray.getString(index) + "\",";
                                                //去除重复的列
                                                if (sbColumn.indexOf(column) == -1) {
                                                    sbColumn.append(column);
                                                    String value = jsonMulti.getString(key);
                                                    sbValue.append("\'" + StringUtil.dealValue(value) + "\',");
                                                }
                                            }
                                        }
                                        //插入一条新的hbase的主键
                                        hbaseMainValue = StringUtil.getOrder(valueAfterArray.getString(indexOf));
                                        sbColumn.append("\"" + hbaseMainColumn + "\",");
                                        sbValue.append("\'" + hbaseMainValue + "\',");
                                        StringUtil.BuilderExecuteUpdate(sbColumn, sbValue, hbaseTable, hbaseConnection);
                                    }
                                }
                            } else {
                                //删除没有同步的数据，有可能多数据的问题
                                for (int i = 0; i < hMVLSize; i++) {
                                    String value = hbaseMainValueList.get(i);
                                    String deleteSQL = "delete from \"" + hbaseTable + "\" where \"" +
                                                       hbaseMainColumn + "\" = \'" + value + "\'";
                                    StringUtil.executeUpdate(hbaseConnection, deleteSQL);
                                }
                                hbaseMainValue = StringUtil.getOrder(valueAfterArray.getString(indexOf));
                                StringBuilder sbColumn = new StringBuilder(sbColumnMysql);
                                StringBuilder sbValue = new StringBuilder(sbValueMysql);
                                sbColumn.append("\"" + hbaseMainColumn + "\",");
                                sbValue.append("\'" + hbaseMainValue + "\',");
                                StringUtil.BuilderExecuteUpdate(sbColumn, sbValue, hbaseTable, hbaseConnection);
                            }
                            sbColumnMysql = null;
                            sbValueMysql = null;
                        }
                        //删除，删除数据
                        else if (eventType == CanalEntry.EventType.DELETE) {
                            //根据case_no查询hbase中的主键
                            indexOf = columnBeforeArray.indexOf(relevanceID);
                            if (indexOf != -1) {
                                hbaseMainValue = valueBeforeArray.getString(indexOf);
                            }
                            String deleteHbase = "delete from \"" + hbaseTable + "\" where \"" + hbaseMainColumn +
                                                 "\" like \'" + hbaseMainValue + ConstantSymbol.SPLIT + "%\'";
                            if (StringUtils.isEmpty(hbaseMainValue)) {
                                deleteHbase = "delete from \"" + hbaseTable + "\" where \"" + objectIdColumn + "\" " +
                                              "is null";
                            }
                            StringUtil.executeUpdate(hbaseConnection, deleteHbase);
                        }
                        StringUtil.outToFile(destination + "-binlog", binlogTime);
                        mapBefore = null;
                        mapAfter = null;
                        columnBeforeArray = null;
                        columnAfterArray = null;
                        valueBeforeArray = null;
                        valueAfterArray = null;
                    }
                }
            }
            header = null;
            rowChage = null;
            eventType = null;
        }
        hbaseConnection.close();
        System.gc();
    }

    public static void mongoAddMongoAction(MongodbAddDataConfig config, String mongodbDatabase, List<String>
        getTableList, MongoCollection oplogCollection, DruidDataSource hbaseDds, DruidDataSource mysqlDds, JSONArray
                                               mongoColumnJSONArray, JSONArray hbaseMongodbColumnJSONArray, String
                                               subSql, JSONArray hbaseMysqlColumnJSONArray, JSONArray
                                               columnJSONArray, JSONArray hbaseColumnJSONArray, String destination)
        throws Exception {
        BsonTimestamp lastTs = config.getTs();
        Connection hbaseConnection = null;
        Connection mysqlConnection = null;
        MongoCursor<Document> cursor = null;
        try {
            //获取当前日志的时间
            Bson filters = StringUtil.getFilters(config, mongodbDatabase, getTableList, "$gt");
            FindIterable<Document> tempDocuments = oplogCollection.find(filters).sort(new BasicDBObject("$natural", -1))
                .limit(1);
            Document tempDocument = new Document();
            for (Document document : tempDocuments) {
                tempDocument = document;
            }
            //判断日志时间是否大于开始时间
            if (tempDocument.size() > 0 && lastTs.compareTo((BsonTimestamp) tempDocument.get("ts")) <= 0) {
                FindIterable<Document> documents = oplogCollection.find(filters).noCursorTimeout(true).sort(new
                    BasicDBObject("$natural", 1));
                hbaseConnection = hbaseDds.getConnection();
                mysqlConnection = mysqlDds.getConnection();
                cursor = documents.iterator();
                if (!documents.iterator().hasNext()) {
                    LOGGER.warn("没有日志信息");
                }
                MongodbAddDataConfig tempConfig = new MongodbAddDataConfig();
                tempConfig.setTs(lastTs);
                while (cursor.hasNext()) {
                    Document document = cursor.next();
                    //转换表名和列名
                    String databaseAndTable = document.getString("ns");
                    String[] strs = databaseAndTable.split("\\.");
                    String database = strs[0];
                    String table = strs[1];
                    //数据库名为空时传输全部数据库，不为空则传输指定数据库
                    if (mongodbDatabase.length() == 0 || (mongodbDatabase.length() != 0 && database.equals
                        (mongodbDatabase))) {
                        //表名为空时传输全部表，不为空则传输指定表
                        if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(table))) {
                            Object op = document.get("op");
                            String action = StringUtil.getAction(op);
                            Document documentValue = (Document) document.get("o");
                            lastTs = (BsonTimestamp) document.get("ts");
                            tempConfig.setTs(lastTs);
                            if (!"c".equals(op)) {
                                String jsonStr = documentValue.toJson();
                                List<JSONObject> jsonMultiList = StringUtil.splitCaseMongoData(jsonStr,
                                    mongoColumnJSONArray);
                                String objectIdValue = JSONObject.parseObject(JsonFlattener.flatten(jsonStr))
                                    .getString(mongoMainColumn);
                                //删除，将mongo的字段置为空
                                if (action.equals(ConstantSymbol.MONGODB_ACTION_DELETE)) {
                                    String selectHbase = "select \"" + hbaseMainColumn + "\" from \"" + hbaseTable +
                                                         "\" where \"" + hbaseMainColumn + "\" like \'" +
                                                         objectIdValue + ConstantSymbol.SPLIT + "%\'";
                                    ResultSet resultSet = hbaseConnection.prepareStatement(selectHbase).executeQuery();
                                    //查询所有的rowkey，然后置为空，没有自然则不作处理
                                    while (resultSet.next()) {
                                        String hbaseMainValue = resultSet.getString(1);
                                        int length = hbaseMongodbColumnJSONArray.size();
                                        Set<String> columnSet = new LinkedHashSet<>();
                                        List<String> valueList = new LinkedList<>();
                                        columnSet.add(hbaseMainColumn);
                                        valueList.add(hbaseMainValue);
                                        for (int i = 0; i < length; i++) {
                                            String column = hbaseMongodbColumnJSONArray.getString(i);
                                            //去除重复的列
                                            if (!columnSet.contains(column)) {
                                                columnSet.add(column);
                                                valueList.add("");
                                            }
                                        }
                                        StringUtil.SetAndListExecuteUpdate(columnSet, valueList, hbaseTable,
                                            hbaseConnection);
                                    }
                                    resultSet = null;
                                }
                                //修改和新增，都是upsert into
                                else {
                                    //根据objectId删除HBASE中mongo的数据，mysql有可能已经插入，所以应该查询
                                    String deleteHbase = "delete from \"" + hbaseTable + "\" where \"" +
                                                         hbaseMainColumn + "\" like \'" + objectIdValue +
                                                         ConstantSymbol.SPLIT + "%\'";
                                    if (StringUtils.isEmpty(objectIdValue)) {
                                        deleteHbase = "delete from \"" + hbaseTable + "\" where \"" + objectIdColumn
                                                      + "\" is null";
                                    }
                                    StringUtil.executeUpdate(hbaseConnection, deleteHbase);
                                    Set<String> columnSetMysql = new LinkedHashSet<>();
                                    List<String> valueListMysql = new LinkedList<>();
                                    //联合mysql重新插入数据
                                    String selectMysql = subSql + " where " + relevanceID + " = \'" + objectIdValue +
                                                         "\'";
                                    ResultSet rSet = mysqlConnection.prepareStatement(selectMysql).executeQuery();
                                    //添加mysql的数据
                                    while (rSet.next()) {
                                        for (int i = 1, length = hbaseMysqlColumnJSONArray.size(); i <= length; i++) {
                                            String column = hbaseMysqlColumnJSONArray.getString(i - 1);
                                            if (column.equals(hbaseMainColumn)) {
                                                column = objectIdColumn;
                                            }
                                            if (!columnSetMysql.contains(column)) {
                                                columnSetMysql.add(column);
                                                String value = StringUtil.dealValue(rSet.getObject(i));
                                                if (column.equals("dcw_id") && value.length() == 0) {
                                                    LOGGER.error("mongo中dcw_id为空" + valueListMysql + "-" +
                                                                 objectIdValue);
                                                }
                                                if (column.equals("objectId") && value.length() == 0) {
                                                    LOGGER.warn("mongo中objectId为空" + valueListMysql + "-" +
                                                                objectIdValue);
                                                }
                                                valueListMysql.add(value);
                                            }
                                        }
                                        //去除重复的ref_id
                                        break;
                                    }
                                    //然后再联合mysql的数据插入,如果mysql没有数据则不错处理
                                    for (JSONObject jsonMulti : jsonMultiList) {
                                        Set<String> jsonMultiKeySet = jsonMulti.keySet();
                                        Set<String> columnSet = new LinkedHashSet<>();
                                        columnSet.addAll(columnSetMysql);
                                        List<String> valueList = new LinkedList<>();
                                        valueList.addAll(valueListMysql);
                                        //添加hbase的主键，objectid和当前时间的组合
                                        String hbaseMainValue = StringUtil.getOrder(objectIdValue);
                                        columnSet.add(hbaseMainColumn);
                                        valueList.add(hbaseMainValue);
                                        //添加mongo的数据
                                        for (String key : jsonMultiKeySet) {
                                            int indexOf = columnJSONArray.indexOf(key);
                                            int keyLength = jsonMulti.getString(key).length();
                                            if (indexOf != -1 && jsonMulti.get(key) != null && keyLength > 0) {
                                                String hbaseColumn = hbaseColumnJSONArray.getString(indexOf);
                                                //去除重复的列名
                                                if (!columnSet.contains(hbaseColumn)) {
                                                    columnSet.add(hbaseColumn);
                                                    String value = jsonMulti.getString(key);
                                                    valueList.add(StringUtil.dealValue(value));
                                                }
                                            }
                                        }
                                        StringUtil.SetAndListExecuteUpdate(columnSet, valueList, hbaseTable,
                                            hbaseConnection);
                                    }
                                    columnSetMysql = null;
                                    valueListMysql = null;
                                    rSet = null;
                                }
                                StringUtil.outToFile(destination + "-oplog", lastTs.getTime());
                                jsonMultiList = null;
                            } else {
                                StringUtil.outToFile(destination + "-oplog", 0);
                            }
                            op = null;
                        }
                    }
                    document = null;
                }
                cursor = null;
                documents = null;
            }
            filters = null;
            tempDocuments = null;
            tempDocument = null;
        } catch (MongoQueryException e) {
            LOGGER.warn(StringUtil.timestamp2Date(((long) lastTs.getInc()) * 1000) + "-重新获取position...", e);
            FindIterable<Document> documentsE = oplogCollection.find().noCursorTimeout(true).sort(new BasicDBObject
                ("$natural", -1)).limit(1);
            MongoCursor<Document> cursorE = documentsE.iterator();
            while (cursorE.hasNext()) {
                Document document = cursorE.next();
                lastTs = (BsonTimestamp) document.get("ts");
                //获取mongo中10分钟前的
                lastTs = new BsonTimestamp(lastTs.getInc() - 600, 1);
                if (lastTs.getInc() - 600 < 0) {
                    lastTs = new BsonTimestamp((int) (System.currentTimeMillis() / 1000), 1);
                }
            }
            cursorE.close();
            LOGGER.warn(StringUtil.timestamp2Date(((long) lastTs.getInc()) * 1000) + "-重新获取position成功！");
        } finally {
            if (hbaseConnection != null) {
                hbaseConnection.close();
            }
            if (mysqlConnection != null) {
                mysqlConnection.close();
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        //写入文件保存位置
        SaveFlag.replaceTs(lastTs);
        //作为常驻进程使用，考虑容错，时间每次都刷新
        config.setTs(lastTs);
        System.gc();
    }

    public static void mysqlAddAction(List<CanalEntry.Entry> entryList, List<TableInfo> tableInfoList, DruidDataSource
        hDds, DruidDataSource mDds, List<String> allTableName, String destination) throws Exception {
        Connection hCon = hDds.getConnection();
        Connection mCon = mDds.getConnection();
        for (CanalEntry.Entry entry : entryList) {
            CanalEntry.Header header = entry.getHeader();
            long binlogTime = header.getExecuteTime();
            String tableName = header.getTableName();
            LOGGER.info(tableName + "-" + (System.currentTimeMillis() / 1000 - binlogTime));
            //表名为空则进入下一次循环
            if (StringUtil.addJuge(entry, tableName)) {
                StringUtil.outToFile(destination, 0);
                continue;
            }
            CanalEntry.RowChange rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            CanalEntry.EventType eventType = rowChage.getEventType();
            for (TableInfo tableInfo : tableInfoList) {
                String getSql = tableInfo.getSql();
                Set<String> tempSet = StringUtil.getTableSet(getSql);
                String table = tableInfo.getTable();
                //hbase表主id
                String hbaseMainID = tableInfo.getHbaseColumnJSONArray().getString(0);
                if (tempSet.contains(tableName)) {
                    //改逗号空格为逗号
                    getSql = getSql.replace(", ", ",");
                    String limit = " limit ";
                    if (getSql.indexOf(limit) != -1) {
                        getSql = getSql.substring(0, getSql.indexOf(limit));
                    }
                    if (StringUtil.isIUD(eventType)) {
                        //合并表和原表
                        if (!getSql.contains("like concat")) {
                            //获取表的别名
                            String alia = StringUtil.getAlia(getSql, tableName);
                            String str = alia + ".";
                            String[] mColumns = getSql.substring("select ".length(), getSql.indexOf(" from")).split
                                (",");
                            //获取当前表写入hbase的mysql的字段
                            JSONArray mCJ = StringUtil.getMysqlColumn(mColumns, alia);
                            //获取当前表hbase中字段名
                            JSONArray hCJ = StringUtil.getHbaseColumn(mColumns, alia);
                            //通过mysql语句获取mysql中主键的列名，不包含别名
                            String whereColumn = getSql.substring(getSql.lastIndexOf("where ") + ("where ").length(),
                                getSql.indexOf(" is not null"));
                            String mysqlMainID = "";
                            String mainAlia = "";
                            if (whereColumn.indexOf(".") != -1) {
                                String[] strs = whereColumn.split("\\.");
                                mysqlMainID = strs[1];
                                mainAlia = strs[0];
                            }
                            List<String> wCL = new ArrayList<>();
                            //mysql关联的主键id
                            String IDColumn = "";
                            String selectConcat = "select concat(";
                            String onMainMySQLID = "";
                            if (getSql.contains("left join") || getSql.contains("inner join")) {
                                if (getSql.startsWith(selectConcat)) {
                                    String concat = getSql.substring(selectConcat.length(), getSql.indexOf(") as "));
                                    String[] concats = concat.split(",");
                                    for (String key : concats) {
                                        if (key.indexOf(str) != -1) {
                                            IDColumn = key.split("\\.")[1];
                                        }
                                    }
                                    wCL.add(IDColumn);
                                    String fromBack = getSql.split("from")[1];
                                    String temp = fromBack.substring(fromBack.indexOf(str), fromBack.length());
                                    IDColumn = temp.substring(str.length(), temp.indexOf(" "));
                                    //第二次添加
                                    wCL.add(IDColumn);
                                } else if (mainAlia.equals(alia)) {
                                    IDColumn = mysqlMainID;
                                } else {
                                    String fromBack = getSql.split("from")[1];
                                    String temp = fromBack.substring(fromBack.indexOf(str), fromBack.length());
                                    IDColumn = temp.substring(str.length(), temp.indexOf(" "));
                                    onMainMySQLID = StringUtil.getOnMainMySQLID(alia, fromBack, mainAlia);
                                }
                            } else {
                                IDColumn = mysqlMainID;
                            }
                            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                                //插入
                                if (eventType == CanalEntry.EventType.INSERT) {
                                    Map<String, JSONArray> mapAfter = WriteMysqlData.getColumnAndValue(rowData
                                        .getAfterColumnsList());
                                    JSONArray columnArray = mapAfter.get(ConstantSymbol.COLUMNS);
                                    JSONArray valueArray = mapAfter.get(ConstantSymbol.VALUES);
                                    JSONArray hVA = new JSONArray();
                                    JSONArray hCA = new JSONArray();
                                    //获取hbase主键的值
                                    String IDValue = StringUtil.getHbaseIDValue(columnArray,
                                        valueArray, IDColumn);
                                    hCA.add(hbaseMainID);
                                    if (onMainMySQLID.length() > 0 && !onMainMySQLID.equals(mysqlMainID)) {
                                        String selectSQL = getSql + " and " + mainAlia + "." + onMainMySQLID + " = " +
                                                           "\'" + IDValue + "\'";
                                        ResultSet resultSet = mCon.prepareStatement(selectSQL).executeQuery();
                                        if (resultSet.next()) {
                                            hVA.add(StringUtil.dealValue(resultSet.getString(1)));
                                        } else {
                                            hVA.add(IDValue);
                                        }
                                    } else {
                                        hVA.add(IDValue);
                                    }
                                    //合并表一对多情况，先删除再增加
                                    if (getSql.startsWith(selectConcat)) {
                                        List<String> values = new ArrayList<>();
                                        for (String column : wCL) {
                                            values.add(StringUtil.getHbaseIDValue(columnArray, valueArray, column));
                                        }
                                        List<String> hbaseWhereList = StringUtil.getHbaseWhereColumnList(wCL, mCJ, hCJ);
                                        List<String> deleteColumns = new ArrayList<>();
                                        deleteColumns.add(mysqlMainID);
                                        List<String> deleteValues = new ArrayList<>();
                                        for (int i = 0; i < hbaseWhereList.size(); i++) {
                                            String hbaseColumn = hbaseWhereList.get(i);
                                            if (hbaseColumn.equals(mysqlMainID)) {
                                                deleteValues.add(values.get(i));
                                                break;
                                            }
                                        }
                                        StringUtil.mysqlConcatDelete(deleteColumns, deleteValues, hCon, table);
                                        //再增加
                                        StringUtil.mysqlConcatInsert(getSql, alia, wCL, values, mCon, hCon, table);
                                    }
                                    //原表和合并表一对一情况
                                    else {
                                        //表映射
                                        for (int k = 0; k < columnArray.size(); k++) {
                                            String column = columnArray.getString(k);
                                            int indexOf = mCJ.indexOf(column);
                                            if (indexOf != -1) {
                                                String hbaseColumn = hCJ.getString(indexOf);
                                                if (hCA.indexOf(hbaseColumn) == -1) {
                                                    hCA.add(hCJ.getString(indexOf));
                                                    hVA.add(StringUtil.dealValue(valueArray.getString(k)));
                                                }
                                            }
                                        }
                                        StringUtil.upsertInto(table, hCA, hVA, hCon);
                                    }
                                    mapAfter = null;
                                    columnArray = null;
                                    valueArray = null;
                                    hVA = null;
                                    hCA = null;
                                }
                                //删除和修改
                                else if (eventType == CanalEntry.EventType.DELETE || eventType == CanalEntry
                                    .EventType.UPDATE) {
                                    Map<String, JSONArray> mapBefore = WriteMysqlData.getColumnAndValue(rowData
                                        .getBeforeColumnsList());
                                    Map<String, JSONArray> mapAfter = WriteMysqlData.getColumnAndValue(rowData
                                        .getAfterColumnsList());
                                    JSONArray columns = mapBefore.get(ConstantSymbol.COLUMNS);
                                    JSONArray valuesBefore = mapBefore.get(ConstantSymbol.VALUES);
                                    JSONArray valuesAfter = mapAfter.get(ConstantSymbol.VALUES);
                                    //获取hbase主键的值
                                    String IDValue = StringUtil.getHbaseIDValue(columns, valuesBefore, IDColumn);
                                    if (StringUtils.isBlank(IDValue)) {
                                        IDValue = StringUtil.getHbaseIDValue(columns, valuesAfter, IDColumn);
                                    }
                                    //合并表一对多情况
                                    if (getSql.startsWith(selectConcat)) {
                                        List<String> values = new ArrayList<>();
                                        //设置值
                                        for (String column : wCL) {
                                            values.add(StringUtil.getHbaseIDValue(columns, valuesBefore, column));
                                        }
                                        if (eventType == CanalEntry.EventType.DELETE) {
                                            List<String> hWL = StringUtil.getHbaseWhereColumnList(wCL, mCJ, hCJ);
                                            List<String> dCs = new ArrayList<>();
                                            List<String> dVs = new ArrayList<>();
                                            dVs.add(mysqlMainID);
                                            for (int i = 0; i < hWL.size(); i++) {
                                                String hbaseColumn = hWL.get(i);
                                                if (hbaseColumn.equals(mysqlMainID)) {
                                                    dCs.add(values.get(i));
                                                    break;
                                                }
                                            }
                                            //删除
                                            StringUtil.mysqlConcatDelete(dCs, dVs, hCon, table);
                                        } else {
                                            //修改
                                            StringUtil.mysqlConcatInsert(getSql, alia, wCL, values, mCon, hCon, table);
                                        }
                                    }
                                    //原表和合并表一对一情况
                                    else {
                                        StringBuilder sbColumn = new StringBuilder("\"" + hbaseMainID + "\",");
                                        StringBuilder sbValue = new StringBuilder("\'" + IDValue + "\',");
                                        if (onMainMySQLID.length() > 0 && !onMainMySQLID.equals(mysqlMainID)) {
                                            String selectSQL = getSql + " and " + mainAlia + "." + onMainMySQLID + " " +
                                                               "= \'" + IDValue + "\'";
                                            ResultSet resultSet = mCon.prepareStatement(selectSQL).executeQuery();
                                            if (resultSet.next()) {
                                                sbValue = new StringBuilder("\'" + StringUtil.dealValue(resultSet
                                                    .getString(1)) + "\',");
                                            }
                                        }
                                        String tempSql = "";
                                        //如果不是主表或者是更新操作
                                        if (tempSet.toString().indexOf(tableName) > 1 ||
                                            eventType == CanalEntry.EventType.UPDATE) {
                                            for (int i = 0; i < columns.size(); i++) {
                                                String column = columns.getString(i);
                                                int indexOf = mCJ.indexOf(column);
                                                if (indexOf != -1) {
                                                    String getStr = hCJ.getString(indexOf);
                                                    if (sbColumn.indexOf(getStr) == -1) {
                                                        sbColumn.append("\"" + getStr + "\",");
                                                        if (eventType == CanalEntry.EventType.DELETE) {
                                                            sbValue.append("\'\',");
                                                        } else {
                                                            sbValue.append("\'" + StringUtil.dealValue(valuesAfter
                                                                .get(i)) + "\',");
                                                        }
                                                    }
                                                }
                                            }
                                            sbColumn = new StringBuilder(sbColumn.substring(0, sbColumn.length() - 1));
                                            sbValue = new StringBuilder(sbValue.substring(0, sbValue.length() - 1));
                                            tempSql = "upsert into \"" + table + "\"(" + sbColumn + ") values(" +
                                                      sbValue + ")";
                                        }
                                        //主表的删除操作
                                        else {
                                            sbColumn = new StringBuilder(sbColumn.substring(0, sbColumn.length() - 1));
                                            sbValue = new StringBuilder(sbValue.substring(0, sbValue.length() - 1));
                                            //包含多个表，为清空字段
                                            if (allTableName.indexOf(table) != allTableName.lastIndexOf(table)) {
                                                //如果是主表则删除，看第二个字段是否一样就知道是否为主表
                                                if (columns.get(1).equals(mCJ.get(1))) {
                                                    tempSql = "delete from \"" + table + "\" " + "where " + sbColumn
                                                              + "=" + sbValue;
                                                }
                                                //不是主表则置为空
                                                else {
                                                    for (int i = 0; i < columns.size(); i++) {
                                                        String column = columns.getString(i);
                                                        int indexOf = mCJ.indexOf(column);
                                                        if (indexOf != -1) {
                                                            String getStr = hCJ.getString(indexOf);
                                                            if (sbColumn.indexOf(getStr) == -1) {
                                                                sbColumn.append(",\"" + getStr + "\"");
                                                                sbValue.append(",\'\'");
                                                            }
                                                        }
                                                    }
                                                    tempSql = "upsert into \"" + table + "\"(" + sbColumn + ") values" +
                                                              "(" + sbValue + ")";
                                                }
                                            } else {
                                                tempSql = "delete from \"" + table + "\" " + "where " + sbColumn +
                                                          "=" + sbValue;
                                            }
                                        }
                                        if (StringUtils.isNotBlank(IDValue)) {
                                            StringUtil.executeUpdate(hCon, tempSql);
                                        }
                                    }
                                }
                                rowData = null;
                            }
                            alia = null;
                            str = null;
                            mColumns = null;
                            mCJ = null;
                            hCJ = null;
                            whereColumn = null;
                            mysqlMainID = null;
                            mainAlia = null;
                            wCL = null;
                            IDColumn = null;
                            selectConcat = null;
                            onMainMySQLID = null;
                        }
                        //拆分表为parent和child
                        else {
                            //获取child_id的mysql中列的名字，不包含表别名
                            String where = " where ";
                            String isNotNull = " is not null";
                            String mysqlID = getSql.substring(getSql.indexOf(where) + where.length(), getSql.indexOf
                                (isNotNull)).split("\\.")[1];
                            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                                if (eventType == CanalEntry.EventType.DELETE) {
                                    Map<String, JSONArray> mapBefore = WriteMysqlData.getColumnAndValue(rowData
                                        .getBeforeColumnsList());
                                    JSONArray columnArray = mapBefore.get(ConstantSymbol.COLUMNS);
                                    JSONArray valueArray = mapBefore.get(ConstantSymbol.VALUES);
                                    int indexOf = columnArray.indexOf(mysqlID);
                                    StringUtil.mysqlAddDeleteHbase(indexOf, valueArray, table, hCon);
                                } else if (eventType == CanalEntry.EventType.UPDATE) {
                                    Map<String, JSONArray> mapBefore = WriteMysqlData.getColumnAndValue(rowData
                                        .getBeforeColumnsList());
                                    JSONArray columnArray = mapBefore.get(ConstantSymbol.COLUMNS);
                                    JSONArray valueBeforeArray = mapBefore.get(ConstantSymbol.VALUES);
                                    Map<String, JSONArray> mapAfter = WriteMysqlData.getColumnAndValue(rowData
                                        .getAfterColumnsList());
                                    JSONArray valueAfterArray = mapAfter.get(ConstantSymbol.VALUES);
                                    int indexOf = columnArray.indexOf(mysqlID);
                                    StringUtil.mysqlAddDeleteHbase(indexOf, valueBeforeArray, table, hCon);
                                    StringUtil.mysqlSplitInsert(indexOf, valueAfterArray, getSql, mysqlID, mCon,
                                        hCon, table);
                                } else if (eventType == CanalEntry.EventType.INSERT) {
                                    Map<String, JSONArray> mapAfter = WriteMysqlData.getColumnAndValue(rowData
                                        .getAfterColumnsList());
                                    JSONArray columnArray = mapAfter.get(ConstantSymbol.COLUMNS);
                                    JSONArray valueArray = mapAfter.get(ConstantSymbol.VALUES);
                                    int indexOf = columnArray.indexOf(mysqlID);
                                    StringUtil.mysqlSplitInsert(indexOf, valueArray, getSql, mysqlID, mCon, hCon,
                                        table);
                                }
                            }
                        }
                        StringUtil.outToFile(destination, binlogTime);
                    }
                    limit = null;
                }
                getSql = null;
                tempSet = null;
                table = null;
                hbaseMainID = null;
            }
            tableName = null;
            header = null;
            rowChage = null;
            eventType = null;
        }
        hCon.close();
        mCon.close();
        System.gc();
    }

    /**
     * Java将Unix时间戳转换成指定格式日期字符串
     *
     * @param timestamp 时间戳 如："1473048265";
     * @return 返回结果 如："2016-09-05 16:06:42";
     */
    public static String timestamp2Date(long timestamp) {
        String formats = "yyyy-MM-dd HH:mm:ss";
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    public static String timestamp2Day(long timestamp) {
        String formats = "yyyy-MM-dd";
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

    public static CanalConnector getClusterConnector(String zkServers, String canalDestination, HbaseInfo hbaseInfo,
                                                     DatabaseInfo databaseInfo) {
        CanalConnector connector = CanalConnectors.newClusterConnector(zkServers, canalDestination, "", "");
        ((ClusterCanalConnector) connector).setSoTimeout(ConstantSymbol.SO_TIMEOUT);
        return retryConnect(connector, hbaseInfo, databaseInfo);
    }

    public static CanalConnector getSingleConnector(String canalHost, int canalPort, String canalDestination,
                                                    HbaseInfo hbaseInfo, DatabaseInfo databaseInfo) {
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalHost, canalPort),
            canalDestination, "", "");
        ((SimpleCanalConnector) connector).setSoTimeout(ConstantSymbol.SO_TIMEOUT);
        return retryConnect(connector, hbaseInfo, databaseInfo);
    }

    public static CanalConnector retryConnect(CanalConnector connector, HbaseInfo hbaseInfo, DatabaseInfo
        databaseInfo) {
        int retryCount = 0;
        String classAndMethod = StringUtil.getClassAndMethod(Thread.currentThread());
        while (true) {
            try {
                if (retryCount > ConstantSymbol.RETRY_COUNT) {
                    LOGGER.error(classAndMethod + "中canal连接" + ConstantSymbol.RETRY_COUNT + "次失败！");
                    break;
                }
                if (retryCount == 0) {
                    connector = connectorConnect(connector, hbaseInfo, databaseInfo);
                } else {
                    connector.connect();
                }
                if (retryCount > 0) {
                    LOGGER.info(classAndMethod + "中canal第" + retryCount + "次尝试重连成功！");
                }
                break;
            } catch (CanalClientException e) {
                if (e.getMessage().indexOf("Connection refused") != -1) {
                    LOGGER.error("canal server 已经停止", e);
                    break;
                }
                connector.disconnect();
                try {
                    Thread.sleep(ConstantSymbol.RETRY_SLEEP);
                } catch (InterruptedException e1) {
                    LOGGER.warn(classAndMethod + "中sleep被中断", e1);
                }
                LOGGER.warn(classAndMethod + "中canal第" + (retryCount++) + "次尝试重连...", e);
            }
        }
        return connector;
    }

    public static CanalConnector getConnector(AddDataConfig config, HbaseInfo hbaseInfo, DatabaseInfo databaseInfo) {
        String canalHost = config.getCanalHost();
        int canalPort = config.getCanalPort();
        String zkServers = config.getCanalZkServers();
        String destination = config.getCanalDestination();
        CanalConnector connector;
        if (StringUtils.isNotEmpty(zkServers)) {
            connector = StringUtil.getClusterConnector(zkServers, destination, hbaseInfo, databaseInfo);
        } else {
            connector = StringUtil.getSingleConnector(canalHost, canalPort, destination, hbaseInfo, databaseInfo);
        }
        return connector;
    }

    public static CanalConnector connectorConnect(CanalConnector connector, HbaseInfo hbaseInfo, DatabaseInfo
        databaseInfo) throws CanalClientException {
        try {
            connector.connect();
        } catch (CanalClientException e) {
            throw e;
        }
        List<TableInfo> tableInfoList = hbaseInfo.getTableInfoList();
        Set<String> tableSet = new HashSet<>();
        List<String> tableList = new ArrayList<>();
        String database = "";
        switch (databaseInfo.getClass().getName()) {
            case MONGDBINFOCLASSNAME:
                String mysqlUrl = ((MongodbInfo) databaseInfo).getMysqlUrl();
                database = mysqlUrl.substring(mysqlUrl.indexOf("/") + "/".length(), mysqlUrl.indexOf("?"));
                break;
            case MYSQLINFOCLASSNAME:
                database = ((MysqlInfo) databaseInfo).getDatabase();
                break;
            default:
                break;
        }
        for (TableInfo tableInfo : tableInfoList) {
            Set<String> tempSet = StringUtil.getTableSet(tableInfo.getSql());
            tableSet.addAll(tempSet);
        }
        for (String table : tableSet) {
            if (table.indexOf(".") != -1) {
                tableList.add(table.replace(".", "\\."));
            } else {
                tableList.add(database + "\\." + table);
            }
        }
        if (tableList.size() < 1) {
            connector.subscribe(database + "\\..*");
        } else {
            connector.subscribe(StringUtils.join(tableList, ","));
        }
        connector.rollback();
        LOGGER.info(StringUtil.getClassAndMethod(Thread.currentThread()) + "获取connector成功！");
        return connector;
    }

    public static String getClassAndMethod(Thread thread) {
        StackTraceElement[] stes = thread.getStackTrace();
        String classNameAndMethodName = "";
        for (StackTraceElement ste : stes) {
            if (ste.getClassName().indexOf("DataToHbase") != -1) {
                classNameAndMethodName = ste.getClassName() + "." + ste.getMethodName();
                break;
            }
        }
        return classNameAndMethodName;
    }

    public static Bson getFilters(MongodbAddDataConfig mongodbAddDataConfig, String getDatabase, List<String>
        getTableList, String gt) {
        Bson filters = Filters.and(new BasicDBObject("ts", new BasicDBObject(gt, mongodbAddDataConfig.getTs())), new
            BasicDBObject("op", new BasicDBObject("$ne", "n")), new BasicDBObject("op", new BasicDBObject("$ne",
            "db")));
        if (getDatabase.length() != 0) {
            //没有指定表则该数据库下所有以及$cmd
            if (getTableList.size() != 0) {
                List<Bson> listBsonOr = new ArrayList<>();
                for (int i = 0, length = getTableList.size(); i < length; i++) {
                    String table = getTableList.get(i);
                    List<Bson> listBsonAnd = new ArrayList<>();
                    listBsonAnd.add(new BasicDBObject("ns", getDatabase + ".$cmd"));
                    listBsonAnd.add(new BasicDBObject("o.create", table));
                    listBsonAnd.add(new BasicDBObject("o.drop", table));
                    listBsonAnd.add(new BasicDBObject("o.renameCollection", table));
                    listBsonOr.add(Filters.and(listBsonAnd));
                    listBsonOr.add(new BasicDBObject("ns", new BasicDBObject("$regex", "^" + getDatabase + "" + "." +
                                                                                       getTableList.get(i))));
                }

                filters = Filters.and(new BasicDBObject("ts", new BasicDBObject(gt, mongodbAddDataConfig.getTs())),
                    new BasicDBObject("op", new BasicDBObject("$ne", "n")), new BasicDBObject("op", new BasicDBObject
                        ("$ne", "db")), Filters.or(listBsonOr));
            } else {
                filters = Filters.and(new BasicDBObject("ts", new BasicDBObject(gt, mongodbAddDataConfig.getTs())),
                    new BasicDBObject("op", new BasicDBObject("$ne", "n")), new BasicDBObject("op", new BasicDBObject
                        ("$ne", "db")), new BasicDBObject("ns", new BasicDBObject("$regex", "^" + getDatabase)));
            }
        }
        return filters;
    }

    /**
     * 拆分mongo数据
     *
     * @param jsonStr
     * @param columnArray
     * @return
     */
    public static List<JSONObject> splitCaseMongoData(String jsonStr, JSONArray columnArray) {
        JSONArray mongoColumnArray = new JSONArray();
        for (int i = 0, length = columnArray.size(); i < length; i++) {
            mongoColumnArray.add(columnArray.getString(i));
        }
        mongoColumnArray.add("patient.disease.details._id");
        JSONObject jsonFlattener = JSONObject.parseObject(JsonFlattener.flatten(jsonStr));
        //将transferedTo拆分为site1和site2
        JSONObject jsonSplitTransferedTo = StringUtil.splitTransferedTo(jsonFlattener);
        //补全数据，同级没有的用空字符串
        JSONObject jsonCompleteData = StringUtil.completeData(jsonSplitTransferedTo, mongoColumnArray);
        //将不需要拆分的字段合并为一个数据
        JSONObject propertisJSONObject = StringUtil.getJSONObject(ConstantSymbol.OTHER_PROPERTIES);
        JSONArray notSplitArray = propertisJSONObject.getJSONArray(ConstantSymbol.NOT_SPLIT);
        JSONObject jsonObject = StringUtil.combineColumn(jsonCompleteData, notSplitArray);
        List<String> keyList = new LinkedList<>();
        keyList.addAll(jsonObject.keySet());
        //排序将同一个对象的放在前后，保证对应关系
        Collections.sort(keyList);
        List<String> outKeyList = keyList;
        Set<String> subKeySet = new LinkedHashSet<>();
        //获取每个key的最长内层
        JSONObject maxJson = new JSONObject();
        JSONObject numJson = null;
        for (String key : keyList) {
            String rBKey = StringUtil.replaceBracketValue(key);
            String subKey = StringUtil.getSubKey(key, "]");
            if (-1 != mongoColumnArray.indexOf(rBKey)) {
                subKeySet.add(subKey);
            }
            numJson = new JSONObject();
            int maxNum = StringUtil.getMaxNum(outKeyList, subKey, numJson);
            maxJson.put(key, maxNum);
            key = null;
            rBKey = null;
            subKey = null;
            numJson = null;
        }
        Map<String, LinkedList<String>> map = new LinkedHashMap<>();
        if (subKeySet.size() == 1) {
            map = new LinkedHashMap<>();
            for (String key : keyList) {
                String rBKey = StringUtil.replaceBracketValue(key);
                String value = jsonObject.getString(key);
                Object obj = map.get(rBKey);
                StringUtil.mapPut(obj, map, value, rBKey);
            }
        } else {
            map = new LinkedHashMap<>();
            JSONObject addKeyJson = new JSONObject();
            for (String key : keyList) {
                String rBKey = StringUtil.replaceBracketValue(key);
                if (-1 != mongoColumnArray.indexOf(rBKey)) {
                    String value = jsonObject.getString(key);
                    if (StringUtil.isMongoValueNotEmpty(value)) {
                        String subKeyNow = StringUtil.getSubKey(key, "]");
                        boolean isIn = true;
                        //最外层的肯定不是最内层
                        if (subKeyNow.length() == 0) {
                            continue;
                        }
                        if (!key.endsWith("]")) {
                            //判断是否为最内层
                            for (String subKey : subKeySet) {
                                //只要有一个是它的内层它就不是最内层
                                if (!subKey.equals(subKeyNow) && subKey.startsWith(subKeyNow)) {
                                    isIn = false;
                                    break;
                                }
                                subKey = null;
                            }
                        }
                        //最内层有多少就添加多少，外层按照内层决定
                        if (isIn) {
                            Object obj = map.get(rBKey);
                            //本身添加
                            StringUtil.mapPut(obj, map, value, rBKey);
                            //添加对应所有外层，上下级关系对应
                            for (String outKey : outKeyList) {
                                String outValue = jsonObject.getString(outKey);
                                String outRBKey = StringUtil.replaceBracketValue(outKey);
                                if (-1 != mongoColumnArray.indexOf(outRBKey)) {
                                    String subOutKeyNow = StringUtil.getSubKey(outKey, "]");
                                    //判断是否是该key的外层
                                    if (!outKey.endsWith("]") && !subKeyNow.equals(subOutKeyNow) && subKeyNow.startsWith
                                        (subOutKeyNow)) {
                                        Object outObj = map.get(outRBKey);
                                        //不是空的值，空字符串可以
                                        if (StringUtil.isMongoValueNotEmpty(outValue)) {
                                            Object ob = addKeyJson.get(outKey);
                                            int count = 0;
                                            if (ob != null) {
                                                count = (int) ob;
                                            }
                                            //该外层的最大内层数
                                            int length = maxJson.getIntValue(outKey);
                                            if (count < length) {
                                                addKeyJson.put(outKey, count + 1);
                                                StringUtil.mapPut(outObj, map, outValue, outRBKey);
                                            }
                                            ob = null;
                                        }
                                        outObj = null;
                                    }
                                    subOutKeyNow = null;
                                }
                                outKey = null;
                                outValue = null;
                                outRBKey = null;
                            }
                            obj = null;
                        }
                        subKeyNow = null;
                    }
                    value = null;
                }
                key = null;
                rBKey = null;
            }
            addKeyJson = null;
            map.remove("patient.disease.details._id");
        }
        jsonStr = null;
        jsonFlattener = null;
        jsonSplitTransferedTo = null;
        jsonCompleteData = null;
        propertisJSONObject = null;
        notSplitArray = null;
        jsonObject = null;
        keyList = null;
        outKeyList = null;
        subKeySet = null;
        maxJson = null;
        numJson = null;
        return StringUtil.mapListToListJSONObject(map);
    }

    /**
     * map转换为list，去除冗余数据
     *
     * @param map
     * @return
     */
    public static List<JSONObject> mapListToListJSONObject(Map<String, LinkedList<String>> map) {
        List<JSONObject> list = new ArrayList<>();
        Set<String> mapKeySet = map.keySet();
        int maxListSize = 0;
        for (String mapKey : mapKeySet) {
            int listSize = map.get(mapKey).size();
            if (listSize > maxListSize) {
                maxListSize = listSize;
            }
            mapKey = null;
        }
        JSONObject jsonObject = null;
        for (int i = 0; i < maxListSize; i++) {
            jsonObject = new JSONObject();
            for (String mapKey : mapKeySet) {
                LinkedList<String> valueList = map.get(mapKey);
                if (valueList.size() > i) {
                    jsonObject.put(mapKey, valueList.get(i));
                }
                mapKey = null;
                valueList = null;
            }
            list.add(jsonObject);
            jsonObject = null;
        }
        map = null;
        mapKeySet = null;
        return list;
    }

    public static void mapPut(Object obj, Map<String, LinkedList<String>> map, String value, String rBKey) {
        value = StringUtil.replaceEscapeCharacter(value);
        if (obj != null) {
            ((LinkedList) obj).add(value);
            map.put(rBKey, (LinkedList) obj);
        } else {
            LinkedList<String> list = new LinkedList<>();
            list.add(value);
            map.put(rBKey, list);
        }
    }


    public static JSONObject getFileJsonObject(String filename) {
        String path = StringUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String propertiesPath;
        if (path.endsWith("/")) {
            propertiesPath = path;
        } else {
            propertiesPath = path.substring(0, path.lastIndexOf("/") + 1);
        }
        propertiesPath += filename;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(propertiesPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuffer stringBuffer = StringUtil.fileInputStreamToStringBuffer(fileInputStream);
        return JSONObject.parseObject(stringBuffer.toString());
    }

    public static boolean isMongoValueNotEmpty(String value) {
        return value != null && !value.equals("[]") && !value.equals("{}");
    }

    public static StringBuilder subStringBuilder(StringBuilder sb, int length) {
        return new StringBuilder(sb.substring(0, sb.length() - length));
    }

    public static String upsertIntoSql(String hbaseTable, StringBuilder sbColumn, StringBuilder sbValue) {
        return "upsert into \"" + hbaseTable + "\"(" + sbColumn + ") values(" + sbValue + ")";
    }

    public static void BuilderExecuteUpdate(StringBuilder sbColumn, StringBuilder sbValue, String hbaseTable,
                                            Connection hbaseConnection) {
        sbColumn = StringUtil.subStringBuilder(sbColumn, 1);
        sbValue = StringUtil.subStringBuilder(sbValue, 1);
        String sql = StringUtil.upsertIntoSql(hbaseTable, sbColumn, sbValue);
        StringUtil.executeUpdate(hbaseConnection, sql.toString());
    }

    public static void SetAndListExecuteUpdate(Set<String> columnSet, List<String> valueList, String hbaseTable,
                                               Connection hbaseConnection) {
        String columnsAndValues = "(\"" + StringUtils.join(columnSet, "\",\"") + "\") values('" + StringUtils.join
            (valueList, "','") + "')";
        String sql = "upsert into \"" + hbaseTable + "\"" + columnsAndValues;
        StringUtil.executeUpdate(hbaseConnection, sql);
    }

    public static String getOrder(String id) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        String order = currentTime;
        return id + ConstantSymbol.SPLIT + order;
    }

    public static int getAllCount(List<Future<String>> futures) {
        int allCount = 0;
        for (Future<String> future : futures) {
            try {
                JSONObject json = JSONObject.parseObject(future.get());
                allCount += Integer.valueOf(json.getString("allCount"));
            } catch (InterruptedException e) {
                LOGGER.error("线程中断出错", e);
            } catch (ExecutionException e) {
                LOGGER.error("执行过程中出错", e);
            }
        }
        return allCount;
    }

    public static int getMaxNum(List<String> outKeyList, String subKey, JSONObject numJson) {
        int maxNum = 1;
        //判断是否为它的内层，统计内层各个的个数
        for (String inKey : outKeyList) {
            String subInKey = StringUtil.getSubKey(inKey, "]");
            String rBInKey = StringUtil.replaceBracketValue(inKey);
            //判断是否为subKey的内层
            if (!subInKey.equals(subKey) && subInKey.startsWith(subKey)) {
                Object obj = numJson.get(rBInKey);
                int num = 1;
                if (null != obj) {
                    num = (int) obj + 1;
                }
                if (maxNum < num) {
                    maxNum = num;
                }
                numJson.put(rBInKey, num);
            }
        }
        return maxNum;
    }

    public static void canal(String destination, String database, String table) {
        int batchSize = 5120;
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.10.156", 11111)
            , destination, "", "");
        connector.connect();
        connector.subscribe(database + "\\." + table);
        connector.rollback();
        int totalEmptyCount = 1200;
        int emptyCount = 0;
        while (emptyCount < totalEmptyCount) {
            // 获取指定数量的数据
            Message message = connector.getWithoutAck(batchSize);
            long batchId = message.getId();
            int size = message.getEntries().size();
            if (batchId == -1 || size == 0) {
                emptyCount++;
//                System.out.println("empty count : " + emptyCount + ";batchId : " + batchId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // 处理失败, 回滚数据
                    connector.rollback(batchId);
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                System.out.printf(destination + "-" + table + "-message[batchId=%s,size=%s] \n", batchId, size);
                List<CanalEntry.Entry> entrys = message.getEntries();
                if (entrys.size() > 0) {
                    emptyCount = 0;
                    System.out.println(database + "\\." + table);
                    printEntry(message.getEntries());
                }
            }
            // 提交确认
            connector.ack(batchId);
        }
    }

    private static void printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry
                .EntryType.TRANSACTIONEND) {
                continue;
            }
            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                    e);
            }

            CanalEntry.EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(), entry.getHeader()
                    .getSchemaName(), entry.getHeader().getTableName(), eventType));

            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    public static void outToFile(String example, long binlogTime) {
        try {
            long lastTimeout = -1;
            if (exampleTimeout.get(example) != null) {
                lastTimeout = exampleTimeout.get(example);
            }
            if (lastTimeout != binlogTime) {
                exampleTimeout.put(example, binlogTime);
                String log = StringUtil.timestamp2Date(System.currentTimeMillis()) + "-binlogTime:" + binlogTime + "\n";
                StringUtil.dailyRollingFile(example);
                //true表示在文件末尾追加
                FileOutputStream fos = new FileOutputStream(getTimeoutFile(example), true);
                fos.write(log.getBytes());
                fos.close();
            }
        } catch (IOException e) {
            LOGGER.warn(e);
        }
    }

    public static String getTimeoutFile(String example) {
        return "timeout/" + example + ".log";
    }

    public static void dailyRollingFile(String example) {
        File f = new File(getTimeoutFile(example));
        long lastModifiedTime = f.lastModified();
        String fileModifiedDay = StringUtil.timestamp2Day(lastModifiedTime);
        long currentTime = System.currentTimeMillis();
        String currentDay = StringUtil.timestamp2Day(currentTime);
        if (!fileModifiedDay.equals(currentDay)) {
            f.renameTo(new File(getTimeoutFile(example) + "." + fileModifiedDay));
        }
    }

    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        /*ServerAddress serverAddress = new ServerAddress("58.241.217.179", 15571);
        List<ServerAddress> addrs = new ArrayList<ServerAddress>();
        addrs.add(serverAddress);
        //MongoCredential.create三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential.createMongoCRCredential("root", "admin", "Dtev1heceVx5#px"
            .toCharArray());
        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(credential);
        MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        //与目标数据库能够建立的最大connection数量为50
        build.connectionsPerHost(50);
        build.socketTimeout(0);
        build.socketKeepAlive(false);
        //如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待
        build.threadsAllowedToBlockForConnectionMultiplier(50);
        build.maxWaitTime(1800000);
        //与数据库建立连接的timeout设置为1分钟
        build.connectTimeout(1800000);
        MongoClientOptions options = build.build();
        //通过连接认证获取MongoDB连接
        MongoClient mongoClient = new MongoClient(addrs, credentials, options);
        //连接到数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase("local");
        MongoCollection collection = mongoDatabase.getCollection("oplog.rs");
        long time1 = System.currentTimeMillis();
        FindIterable<Document> findIterable = collection.find(new BasicDBObject("ns", "hbase.cases"));
        //如果有mongo的数据则数量按照mongo来
        for (Document doc : findIterable) {
            Document documentValue = (Document) doc.get("o");
            String objectIdValue = JSONObject.parseObject(JsonFlattener.flatten(documentValue.toJson())).getString
            ("_id" + ".$oid");
            System.out.println(objectIdValue);
        }*/
        /*String filename = "splitCaseMongoData";
        JSONObject jsonObject = StringUtil.getFileJsonObject(filename);
        JSONArray mongoArray = jsonObject.getJSONArray("mongodb.columns");
        List<JSONObject> list = StringUtil.splitCaseMongoData(jsonObject.getString("mongo.data"), mongoArray);
        System.out.println(list.size());
        */
        /*new Thread(() -> {
            canal("example", "glaze_safe", "dcw_base");
        }).start();
        new Thread(() -> {
            canal("example1", "glaze_safe", "da_member");
        }).start();
        new Thread(() -> {
            canal("example2", "project_kb", "article");
        }).start();*/
        /*new Thread(() -> {
            canal("example3", "project_kb_cn", "kt_disease");
        }).start();
        new Thread(() -> {
            canal("example4", "project_kb_en", "kt_disease");
        }).start();*/
        /*
        new Thread(() -> {
            canal("example1", "dcw_index");
        }).start();*/
        /*String sql = "upsert into \"table\"(\"id\",\"text\") values(\'1\',  \'12\')";
        int valuesIndexOf = sql.indexOf("\") values(\'");
        String[] values = sql.substring(valuesIndexOf + 10, sql.length() - 1).split(",");
        int tableIndexOf = sql.indexOf("\"(");
        //把"包括
        StringBuilder upsertSQL = new StringBuilder(sql.substring(0, tableIndexOf + 1));
        StringBuilder valueSb = new StringBuilder(" values(");
        for (int i = 0; i < values.length; i++) {
            valueSb.append("?,");
        }
        //去掉逗号
        upsertSQL.append(valueSb.substring(1, valueSb.length() - 1));
        upsertSQL.append(")");
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            System.out.println(value.substring(1, value.length() - 1));
        }*/
//        System.out.println(readToString("C:/Users/zgh/Desktop/1.json"));
//        System.out.println(formatJSON(readToString("C:/Users/zgh/Desktop/1.json")));
        /*String sql = "upsert into \"user\"(\"id\",\"company_id\",\"department_id\",\"territory_id\",\"area_id\"," +
                     "\"role_id\",\"login_name\",\"password\",\"no\",\"email\",\"phone\",\"mobile\",\"user_type\"," +
                     "\"status\"," +
                     "\"points\",\"avatar\",\"is_disabled\",\"login_ip\",\"last_login_date_time\",\"login_flag\"," +
                     "\"creator\"," +
                     "\"create_date_time\",\"updater\",\"update_date_time\",\"remarks\",\"delete_flag\",\"open_id\"," +
                     "\"qr_code_ticket\",\"send_money_number\",\"is_leave\",\"leave_date_time\",\"entry_date_time\") " +
                     "values" +
                     "('845c107179b64d42b00d2374323e478d','','e92fdc186f604f038673db0c00a1a640','','1430'," +
                     "'44357c44082d4c68b1d2ef2110c22c58','sdwangguoqiang'," +
                     "'333d3ade8f7b51af20b7cdbed2efb824211aaaec7fccd70c218c244e','/','','','13276495565','','0',''," +
                     "'avatar_1533265851773lYWmvydBcfWyAgNa.jpg','0','112.224.2.92, 101.71.140.14, 49.7.6.195, " +
                     "49.7.6.195','2018-09-06 11:42:01','1','074de1e950b14c6abcc85f5669b9f827','2018-08-03 08:49:30'," +
                     "'845c107179b64d42b00d2374323e478d','2018-09-07 09:08:03','','0',''," +
                     "'gQGe8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyRUtseWNoOWE5WVAxMDAwMDAwN1kAAgRpx2NbAwQAAAAA" +
                     "','','0','','2018-08-08 08:00:00')";
        int valuesIndexOf = sql.indexOf("\") values(\'");
        String[] values = sql.substring(valuesIndexOf + 10, sql.length() - 1).split(",");
        System.out.println(values.length);*/
    }
}
