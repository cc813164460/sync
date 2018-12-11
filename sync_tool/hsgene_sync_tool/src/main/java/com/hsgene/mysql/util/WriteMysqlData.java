package com.hsgene.mysql.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.hsgene.constant.ConstantSymbol;
import com.hsgene.model.AddDataConfig;
import com.hsgene.model.DataSendToKafka;
import com.hsgene.model.DatabaseInfo;
import com.hsgene.model.TargetInfo;
import com.hsgene.mysql.model.MysqlInfo;
import com.hsgene.util.AbstractWriteData;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.log4j.Logger;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:54 2017/12/12
 * @Modified By:
 */
public class WriteMysqlData extends AbstractWriteData {

    private final static Logger LOGGER = Logger.getLogger(WriteMysqlData.class);
    public DruidDataSource writeMysqlDds;

    void initConnection(MysqlInfo mysqlInfo) {
        try {
            String sourceUrl = setMysqlSourceUrl(mysqlInfo);
            int end = sourceUrl.indexOf("?");
            String mysqlUrl = sourceUrl.substring(0, end);
            initConnection(mysqlInfo, mysqlUrl);
        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    void initConnection(MysqlInfo mysqlInfo, String mysqlUrl) {
        try {
            writeMysqlDds = getDruidDataSource(ConstantSymbol.MYSQL_DRIVERCLASS, mysqlInfo.getUser(), mysqlInfo
                .getPassword(), mysqlUrl, initialSize, initialSize, maxActive, "stat", false);
        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    public void writeAllData(DatabaseInfo databaseInfo, TargetInfo targetInfo) {
        switch (targetInfo.getType()) {
            case ConstantSymbol.KAFKA:
                new WriteMysqlDataToKafka().writeAllData(databaseInfo, targetInfo);
                break;
            case ConstantSymbol.HBASE:
                new WriteMysqlDataToHbase().writeAllData(databaseInfo, targetInfo);
                break;
            default:
                break;
        }

    }

    public void writeAddData(DatabaseInfo databaseInfo, TargetInfo targetInfo, AddDataConfig addDataConfig) {
        switch (targetInfo.getType()) {
            case ConstantSymbol.KAFKA:
                new WriteMysqlDataToKafka().writeAddData(databaseInfo, targetInfo, addDataConfig);
                break;
            case ConstantSymbol.HBASE:
                new WriteMysqlDataToHbase().writeAddData(databaseInfo, targetInfo, addDataConfig);
                break;
            default:
                break;
        }
    }

    List<String> getAllTableName() {
        try {
            List<String> tablenameList = new ArrayList<>();
            Connection connection = writeMysqlDds.getConnection();
            ResultSet resultSet = connection.prepareStatement("show tables").executeQuery();
            while (resultSet.next()) {
                tablenameList.add(resultSet.getString(1));
            }
            return tablenameList;
        } catch (SQLException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
        return null;
    }

    String setMysqlSourceUrl(MysqlInfo mysqlInfo) {
        StringBuilder sourceUrl = new StringBuilder(ConstantSymbol.MYSQL_JDBC);
        sourceUrl.append(mysqlInfo.getHost());
        sourceUrl.append(":");
        sourceUrl.append(mysqlInfo.getPort());
        sourceUrl.append("/");
        sourceUrl.append(mysqlInfo.getDatabase());
        sourceUrl.append("?");
        sourceUrl.append("user=");
        sourceUrl.append(mysqlInfo.getUser());
        sourceUrl.append("&");
        sourceUrl.append("password=");
        sourceUrl.append(mysqlInfo.getPassword());
        return sourceUrl.toString();
    }

    static Map<String, List<String>> getColumn(String str) {
        List<String> addfields = new ArrayList<>();
        List<String> oldfields = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        List<String> dropfields = new ArrayList<>();
        List<String> renamefields = new ArrayList<>();
        String[] strs = str.split("\n");
        for (int i = 0; i < strs.length; i++) {
            if (strs[i].contains("ADD COLUMN")) {
                int length = strs[i].indexOf("ADD COLUMN `") + "ADD COLUMN `".length();
                String column = strs[i].substring(length, strs[i].indexOf("` "));
                addfields.add(column);
            } else if (strs[i].contains("CHANGE COLUMN")) {
                int length = strs[i].indexOf("CHANGE COLUMN `") + "CHANGE COLUMN `".length();
                String column1 = strs[i].substring(length, strs[i].indexOf("`", length));
                int length1 = strs[i].indexOf("` `") + "` `".length();
                String column2 = strs[i].substring(length1, strs[i].indexOf("` ", length1));
                oldfields.add(column1);
                fields.add(column2);
            } else if (strs[i].contains("DROP COLUMN")) {
                int length = strs[i].indexOf("DROP COLUMN `") + "DROP COLUMN `".length();
                String column = strs[i].substring(length, strs[i].indexOf("`", length));
                dropfields.add(column);
            } else if (strs[i].contains("RENAME")) {
                int length = strs[i].indexOf("RENAME `") + "RENAME `".length();
                String column = strs[i].substring(length, strs[i].indexOf("`", length));
                renamefields.add(column);
            }
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put(ConstantSymbol.ADDFIELDS, addfields);
        map.put(ConstantSymbol.OLDFIELDS, oldfields);
        map.put(ConstantSymbol.FIELDS, fields);
        map.put(ConstantSymbol.DROPFIELDS, dropfields);
        map.put(ConstantSymbol.RENAMEFIELDS, renamefields);
        return map;
    }

    public static Map<String, JSONArray> getColumnAndValue(List<CanalEntry.Column> columns) {
        Map<String, JSONArray> map = new HashMap<>();
        JSONArray columnArray = new JSONArray();
        JSONArray valueArray = new JSONArray();
        for (CanalEntry.Column column : columns) {
            columnArray.add(column.getName());
            String mysqlType = column.getMysqlType().replace("'", "");
            String value = column.getValue();
            if (mysqlType.contains("enum")) {
                int index = Integer.valueOf(value) - 1;
                String[] enums = mysqlType.substring(mysqlType.indexOf("(") + "(".length(), mysqlType.length() - 1)
                    .split(",");
                valueArray.add(enums[index]);
            } else {
                valueArray.add(value);
            }

        }
        map.put(ConstantSymbol.COLUMNS, columnArray);
        map.put(ConstantSymbol.VALUES, valueArray);
        return map;
    }

    /**
     *处理数据增删改的sql
     */
    void getDealSQLInfo(String sqlInfo, DataSendToKafka dataSendToKafka, String databaseName, List<String>
        getTableList) {
        try {
            String action = "";
            String table = "";
            StringBuilder databaseAndTable = new StringBuilder();
            JSONObject jsonObject = new JSONObject();
            //去除末尾的limit
            int blankIndex = sqlInfo.toLowerCase().lastIndexOf(" ");
            if (blankIndex != -1) {
                if (sqlInfo.substring(0, blankIndex).toLowerCase().endsWith("limit")) {
                    sqlInfo = sqlInfo.substring(0, blankIndex);
                    sqlInfo = sqlInfo.substring(0, sqlInfo.length() - 5);
                }
            }
            net.sf.jsqlparser.statement.Statement statementFormat = new CCJSqlParserManager().parse(new StringReader
                (sqlInfo));
            if (statementFormat instanceof Update) {
                Update update = (Update) statementFormat;
                table = update.getTables().get(0).toString().replace("`", "");
                //表名为空时传输全部表，不为空则传输指定表
                if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(table))) {
                    action = ConstantSymbol.ACTION_TYPE_UPDATE;
                    List<String> columns = new LinkedList<>();
                    setList(update, columns);
                    List<String> values = new LinkedList<>();
                    List<Expression> expressions = update.getExpressions();
                    setExpressionList(expressions, values);
                    jsonObject.put(ConstantSymbol.COLUMNS, columns);
                    jsonObject.put(ConstantSymbol.VALUES, values);
                    String where = update.getWhere().toString();
                    List<String> wherecolumns = new LinkedList<>();
                    List<String> wherevalues = new LinkedList<>();
                    String[] wheres = where.split(" ");
                    String whereName = wheres[0].replace("(", "").replace("`", "");
                    String whereValue = wheres[2].replace(")", "").replace("'", "").replace("\"", "");
                    wherecolumns.add(whereName);
                    wherevalues.add(whereValue);
                    jsonObject.put(ConstantSymbol.WHERECOLUMNS, wherecolumns);
                    jsonObject.put(ConstantSymbol.WHEREVALUES, wherevalues);
                }
            } else if (statementFormat instanceof Delete) {
                Delete delete = (Delete) statementFormat;
                table = delete.getTable().toString().replace("`", "");
                //表名为空时传输全部表，不为空则传输指定表
                if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(table))) {
                    action = ConstantSymbol.ACTION_TYPE_DELETE;
                    String where = delete.getWhere().toString();
                    List<String> columns = new LinkedList<>();
                    List<String> values = new LinkedList<>();
                    String[] wheres = where.split(" ");
                    String whereName = wheres[0].replace("(", "").replace("`", "");
                    String whereValue = wheres[2].replace(")", "").replace("'", "").replace("\"", "");
                    columns.add(whereName);
                    values.add(whereValue);
                    jsonObject.put(ConstantSymbol.COLUMNS, columns);
                    jsonObject.put(ConstantSymbol.VALUES, values);
                }
            } else if (statementFormat instanceof Insert) {
                Insert insert = (Insert) statementFormat;
                table = insert.getTable().toString().replace("`", "");
                //表名为空时传输全部表，不为空则传输指定表
                if (getTableList.size() == 0 || (getTableList.size() != 0 && getTableList.contains(table))) {
                    action = ConstantSymbol.ACTION_TYPE_INSERT;
                    List<String> columns = new LinkedList<>();
                    setList(insert, columns);
                    List<String> values = new LinkedList<>();
                    List<Expression> expressions = ((ExpressionList) insert.getItemsList()).getExpressions();
                    setExpressionList(expressions, values);
                    //考虑insert system_user VALUES("6", "6", "6")
                    jsonObject.put(ConstantSymbol.COLUMNS, columns);
                    jsonObject.put(ConstantSymbol.VALUES, values);
                }
            }
            databaseAndTable.append(databaseName);
            databaseAndTable.append(".");
            databaseAndTable.append(table.replace("`", ""));
            dataSendToKafka.setTable(databaseAndTable.toString());
            dataSendToKafka.setAction(action);
            if (jsonObject.size() > 0) {
                dataSendToKafka.setData(jsonObject);
            }
        } catch (Exception e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    void setList(net.sf.jsqlparser.statement.Statement statement, List<String> list) {
        List<Column> columnList = new ArrayList<>();
        if (statement instanceof Update) {
            columnList = ((Update) statement).getColumns();
        } else if (statement instanceof Insert) {
            columnList = ((Insert) statement).getColumns();
        }
        if (columnList != null && columnList.size() > 0) {
            for (int i = 0, length = columnList.size(); i < length; i++) {
                Column column = columnList.get(i);
                String str = column.toString();
                if (str.startsWith("`")) {
                    str = str.substring(1, str.length() - 1);
                }
                list.add(str);
            }
        }
    }

    void setDataList(String line, List<String> list) {
        line = line.substring("###   ".length(), line.length());
        if (line.startsWith("@")) {
            String value = line.substring(line.indexOf("=") + 1, line.length());
            if (value.startsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            list.add(value);
        }
    }

    void setExpressionList(List<Expression> expressions, List<String> expressionList) {
        for (int i = 0, length = expressions.size(); i < length; i++) {
            String str = expressions.get(i).toString();
            if (str.startsWith("'") || str.startsWith("\"")) {
                str = str.substring(1, str.length() - 1);
            }
            expressionList.add(str);
        }
    }

}
