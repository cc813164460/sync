package com.hsgene.elasticsearch.sync.util;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @projectName: sync_elasticsearch
 * @package: com.hsgene.elasticsearch.sync.util
 * @author: maodi
 * @createDate: 2018/12/4 17:31
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ElasticSearchClientFactory implements PooledObjectFactory<TransportClient> {

    private final static Logger LOGGER = LogManager.getLogger(ElasticSearchClientFactory.class);

    /**
     * elasticsearch集群配置名称
     */
    private final static String CLUSTER_NAME = "cluster.name";

    /**
     * elasticsearch集群名称
     */
    private String clusterName;

    /**
     * elasticsearch集群节点链表
     */
    List<String> clusterNodes = new ArrayList<>();

    public ElasticSearchClientFactory(String clusterName, List<String> clusterNodes) {
        this.clusterName = clusterName;
        this.clusterNodes = clusterNodes;
    }

    @Override
    public PooledObject<TransportClient> makeObject() {
        try {
            Settings settings = Settings.builder().put(CLUSTER_NAME, clusterName).build();
            TransportClient client = new PreBuiltTransportClient(settings);
            for (String clusterNode : clusterNodes) {
                String[] hostAndPort = clusterNode.split(":");
                String host = hostAndPort[0];
                int port = Integer.valueOf(hostAndPort[1]);
                client.addTransportAddresses(new TransportAddress(InetAddress.getByName(host), port));
            }
            return new DefaultPooledObject(client);
        } catch (NumberFormatException e) {
            LOGGER.error("连接elasticsearch时出错，端口转换为数字失败", e);
            e.printStackTrace();
            return null;
        } catch (UnknownHostException e) {
            LOGGER.error("连接elasticsearch时出错，无法识别的地址", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void destroyObject(PooledObject<TransportClient> pooledObject) throws Exception {
        TransportClient client = pooledObject.getObject();
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOGGER.error("关闭elasticsearch连接出错", e);
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<TransportClient> pooledObject) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<TransportClient> pooledObject) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<TransportClient> pooledObject) throws Exception {
    }


}
