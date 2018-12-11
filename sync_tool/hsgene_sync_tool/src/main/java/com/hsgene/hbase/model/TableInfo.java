package com.hsgene.hbase.model;

import com.alibaba.fastjson.JSONArray;
import com.hsgene.util.StringUtil;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 16:07 2017/12/27
 * @Modified By:
 */
public class TableInfo {
    private String mongodbColumns;
    private String hbaseColumns;
    private String sql;
    private String table;
    private String columnFamily;

    public String getMongodbColumns() {
        return mongodbColumns;
    }

    public void setMongodbColumns(String mongodbColumns) {
        this.mongodbColumns = mongodbColumns;
    }

    public String getHbaseColumns() {
        return hbaseColumns;
    }

    public void setHbaseColumns(String hbaseColumns) {
        this.hbaseColumns = hbaseColumns;
    }

    public JSONArray getHbaseColumnJSONArray() {
        if (hbaseColumns.length() == 0) {
            return new JSONArray();
        }
        return JSONArray.parseArray(hbaseColumns);
    }

    public JSONArray getHbaseMysqlColumnJSONArray() {
        if (sql.length() == 0) {
            return new JSONArray();
        }
        String sql = getSql();
        String alia = StringUtil.getAlia(sql);
        sql = sql.substring("select ".length(), sql.indexOf(" from "));
        String[] columns = sql.split(",");
        JSONArray jsonArray = new JSONArray();
        for (String column : columns) {
            if (column.startsWith(alia + ".")) {
                jsonArray.add(column.substring(column.indexOf(" as ") + " as ".length(), column.length()));
            }
        }
        return jsonArray;
    }

    public JSONArray getHbaseMongodbColumnJSONArray() {
        JSONArray hbaseColumnsArray = getHbaseColumnJSONArray();
        JSONArray hbaseMysqlColumnsArray = getHbaseMysqlColumnJSONArray();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0, length = hbaseColumnsArray.size(); i < length; i++) {
            String column = hbaseColumnsArray.getString(i);
            if (!hbaseMysqlColumnsArray.contains(column)) {
                jsonArray.add(column);
            }
        }
        return jsonArray;
    }

    public JSONArray getMysqlColumnJSONArray() {
        if (sql.length() == 0) {
            return new JSONArray();
        }
        String sql = getSql();
        String alia = StringUtil.getAlia(sql);
        sql = sql.substring("select ".length(), sql.indexOf(" from "));
        String[] columns = sql.split(",");
        JSONArray jsonArray = new JSONArray();
        for (String column : columns) {
            if (column.startsWith(alia + ".")) {
                jsonArray.add(column.substring(column.indexOf(".") + ".".length(), column.indexOf(" as ")));
            }
        }
        return jsonArray;
    }

    public JSONArray getMongodbColumnJSONArray() {
        if (mongodbColumns.length() == 0) {
            return new JSONArray();
        }
        return JSONArray.parseArray(mongodbColumns);
    }

    public JSONArray getColumnJSONArray() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(getHbaseMysqlColumnJSONArray());
        jsonArray.addAll(getMongodbColumnJSONArray());
        return jsonArray;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql.replace(", ", ",");
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
               "mongodbColumns='" + mongodbColumns + '\'' +
               "hbaseColumns='" + hbaseColumns + '\'' +
               ", sql='" + sql + '\'' +
               ", table='" + table + '\'' +
               ", columnFamily='" + columnFamily + '\'' +
               '}';
    }
}
