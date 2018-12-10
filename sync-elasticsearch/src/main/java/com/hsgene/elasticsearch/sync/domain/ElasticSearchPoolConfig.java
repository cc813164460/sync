package com.hsgene.elasticsearch.sync.domain;

import com.hsgene.elasticsearch.sync.util.StringUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @projectName: sync_elasticsearch
 * @package: com.hsgene.elasticsearch.sync.domain
 * @author: maodi
 * @createDate: 2018/12/4 18:22
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ElasticSearchPoolConfig extends GenericObjectPoolConfig implements Serializable {

    public static final int DEFAULT_MAX_TOTAL = StringUtil.POOL_NUM;
    public static final int DEFAULT_MAX_IDLE = StringUtil.POOL_NUM;
    public static final int DEFAULT_MIN_IDLE = 0;
    private int maxTotal = StringUtil.POOL_NUM;
    private int maxIdle = StringUtil.POOL_NUM;
    private int minIdle = 0;

    private String clusterName;

    private List<String> clusterNodes;

    public ElasticSearchPoolConfig(String clusterName, List<String> clusterNodes) {
        this.clusterName = clusterName;
        this.clusterNodes = clusterNodes;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(List<String> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public int getMaxTotal() {
        return maxTotal;
    }

    @Override
    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    @Override
    public int getMaxIdle() {
        return maxIdle;
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    @Override
    public int getMinIdle() {
        return minIdle;
    }

    @Override
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }
}