package com.hsgene.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 10:03 2017/10/26
 * @Modified By:
 */
public class DataSendToKafka {

    private String source;
    private String action;
    private String table;
    private Object data;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        if (data == null) {
            return "{\"source\":\"" + source + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"table\":\"" + table + '\"' +
                "}";
        } else {
            return "{\"source\":\"" + source + '\"' +
                ", \"action\":\"" + action + '\"' +
                ", \"table\":\"" + table + '\"' +
                ", \"data\":\"" + data +
                "\"}";
        }
    }

}
