package com.hsgene.model;

import java.util.List;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 10:19 2017/10/26
 * @Modified By:
 */
public class UrlInfos {

    private List<UrlInfo> urlInfoList;

    public List<UrlInfo> getUrlInfoList() {
        return urlInfoList;
    }

    public void setUrlInfoList(List<UrlInfo> urlInfoList) {
        this.urlInfoList = urlInfoList;
    }

    @Override
    public String toString() {
        return "UrlInfos{" + "urlInfoList=" + urlInfoList + '}';
    }

}
