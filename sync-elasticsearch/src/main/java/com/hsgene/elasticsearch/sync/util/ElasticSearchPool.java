package com.hsgene.elasticsearch.sync.util;

import com.hsgene.elasticsearch.sync.domain.ElasticSearchPoolConfig;
import com.hsgene.elasticsearch.sync.util.ElasticSearchClientFactory;
import com.hsgene.elasticsearch.sync.util.Pool;
import org.elasticsearch.client.transport.TransportClient;

import java.util.List;

/**
 * @description:
 * @projectName: sync_elasticsearch
 * @package: com.hsgene.elasticsearch.sync.client
 * @author: maodi
 * @createDate: 2018/12/4 17:28
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ElasticSearchPool extends Pool<TransportClient> {

    private String clusterName;
    private List<String> clusterNodes;

    public ElasticSearchPool(String clusterName, List<String> clusterNodes) throws Exception{
        super(new ElasticSearchPoolConfig(clusterName, clusterNodes), new ElasticSearchClientFactory(clusterName,
                clusterNodes));
        this.clusterName = clusterName;
        this.clusterNodes = clusterNodes;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(List<String> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }
}
