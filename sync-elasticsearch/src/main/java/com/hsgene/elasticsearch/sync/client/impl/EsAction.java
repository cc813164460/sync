package com.hsgene.elasticsearch.sync.client.impl;

import com.hsgene.elasticsearch.sync.util.ElasticSearchPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @projectName: sync-elasticsearch
 * @package: com.hsgene.elasticsearch.sync.client.impl
 * @author: maodi
 * @createDate: 2018/12/7 9:15
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class EsAction {

    private final static Logger LOGGER = LogManager.getLogger(EsAction.class);

    private final static String MAPPING = "{\"properties\":{\"id\":{\"type\":\"text\"}," +
                                          "\"name\":{\"type\":\"text\"}," +
                                          "\"medicines\":{\"properties\":{\"medicineId\":{\"type\":\"text\"}," +
                                          "\"medicineName\":{\"type\":\"text\"}}},\"targets\":{\"type\":\"text\"}," +
                                          "\"detection\":{\"type\":\"text\"},\"clinical\":{\"type\":\"text\"}," +
                                          "\"price\":{\"type\":\"double\"},\"commission\":{\"type\":\"integer\"}," +
                                          "\"cancers\":{\"properties\":{\"id\":{\"type\":\"text\"}," +
                                          "\"name\":{\"type\":\"text\"},\"category\":{\"type\":\"text\"}}}," +
                                          "\"nature\":{\"type\":\"text\"},\"description\":{\"type\":\"text\"}," +
                                          "\"orgs\":{\"type\":\"text\"},\"usageCount\":{\"type\":\"integer\"}," +
                                          "\"number\":{\"type\":\"integer\"},\"updateDateTime\":{\"type\": \"text\"}," +
                                          "\"cartStatus\":{\"type\":\"integer\"},\"testPeriod\":{\"type\":\"text\"}," +
                                          "\"integration\":{\"type\":\"integer\"}," +
                                          "\"policyType\":{\"type\":\"integer\"},\"isUsed\":{\"type\":\"integer\"}}}";

    /**
     * elasticsearch连接池
     */
    private ElasticSearchPool pool;

    /**
     * elasticsearch连接
     */
    private TransportClient client;

    public EsAction(ElasticSearchPool pool) {
        this.pool = pool;
        if (client == null) {
            try {
                client = pool.getResource();
            } catch (Exception e) {
                LOGGER.error("获取elasticsearch连接出错", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * @param index 索引
     * @param type  类型
     * @return void
     * @description 设置elasticsearch映射
     * @author maodi
     * @createDate 2018/12/7 9:27
     */
    public void putEsMapping(String index, String type) {
        try {
            CreateIndexRequestBuilder cirb = client.admin().indices().prepareCreate(index);
            CreateIndexResponse response = cirb.addMapping(type, MAPPING, XContentType.JSON).execute().actionGet();
            boolean isAcknowledged = response.isAcknowledged();
            if (isAcknowledged) {
                LOGGER.info("创建elasticsearch的" + index + "索引，" + type + "类型的映射成功");
            } else {
                String error = "创建elasticsearch的" + index + "索引，" + type + "类型的映射失败";
                LOGGER.error(error);
                throw new Exception(error);
            }
        } catch (Exception e) {
            LOGGER.error("设置elasticsearch映射配置出错", e);
            e.printStackTrace();
        }
    }

    /**
     * @param index 索引
     * @return void
     * @description 删除索引下所有数据
     * @author maodi
     * @createDate 2018/12/7 9:23
     */
    public void deleteEsData(String index) {
        try {
            DeleteByQueryAction.INSTANCE.newRequestBuilder(client).source(index).filter(QueryBuilders.matchAllQuery()
            ).execute().actionGet();
            LOGGER.info("删除elasticsearch " + index + "索引下所有数据成功");
        } catch (Exception e) {
            LOGGER.error("删除elasticsearch索引下所有数据出错", e);
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return void
     * @description 归还elasticsearch连接
     * @author maodi
     * @createDate 2018/12/7 9:22
     */
    public void returnResource() {
        try {
            pool.returnResource(client);
        } catch (Exception e) {
            LOGGER.error("归还elasticsearch连接出错", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String index = "yinzigene_test";
            List<String> clusterNodes = new ArrayList<>();
            clusterNodes.add("172.30.10.160:9300");
            ElasticSearchPool esPool = new ElasticSearchPool("AI-Cluster", clusterNodes);
            EsAction action = new EsAction(esPool);
            action.deleteEsData(index);
            action.putEsMapping(index, "product");
            action.returnResource();
            esPool.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
