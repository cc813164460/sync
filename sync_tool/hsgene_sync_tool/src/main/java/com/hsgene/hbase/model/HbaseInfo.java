package com.hsgene.hbase.model;

import java.util.List;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:25 2017/12/12
 * @Modified By:
 */
public class HbaseInfo {
    private String url;
    private List<TableInfo> tableInfoList;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    public void setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
    }

    @Override
    public String toString() {
        return "HbaseInfo{" +
               "url='" + url + '\'' +
               ", tableInfoList='" + tableInfoList + '\'' +
               '}';
    }
}
