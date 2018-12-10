package com.hsgene.elasticsearch.sync;

import com.hsgene.elasticsearch.sync.client.SyncData;
import com.hsgene.elasticsearch.sync.client.impl.EsToEsSync;
import com.hsgene.elasticsearch.sync.client.impl.MysqlToEsSync;
import com.hsgene.elasticsearch.sync.domain.SyncType;
import com.hsgene.elasticsearch.sync.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @description: 启动主类
 * @projectName: sync_elasticsearch
 * @package: sync
 * @author: maodi
 * @createDate: 2018/12/3 16:12
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class SyncElasticsearchApp {

    /**
     * 配置文件路径
     */
    private static final String PROPERTIES_PATH = "elasticsearch.properties";

    private final static Logger LOGGER = LogManager.getLogger(SyncElasticsearchApp.class);

    public static void main(String[] args) {
        Properties prop = new Properties();
        try {
            LOGGER.info("正在加载配置信息...");
            FileInputStream in = new FileInputStream(StringUtil.getAbsolutePath(PROPERTIES_PATH));
            prop.load(in);
            LOGGER.info("加载配置信息成功");
        } catch (Exception e) {
            LOGGER.error("加载配置信息出错，请确认配置文件名称及路径是否正确", e);
            e.printStackTrace();
        }
        String type = null;
        try {
            type = prop.getProperty("sync.type");
        } catch (Exception e) {
            LOGGER.error("获取同步类型出错", e);
            e.printStackTrace();
        }
        if (StringUtils.isBlank(type)) {
            String error = "获取同步类型出错，请确认sync.type是否配置正确";
            LOGGER.error(error);
            throw new IllegalArgumentException(error);
        }
        try {
            SyncData sync;
            if (SyncType.MYSQL_TO_ELASTICSEARCH.getType().equalsIgnoreCase(type)) {
                sync = new MysqlToEsSync(prop);
            } else if (SyncType.ELASTICSEARCH_TO_ELASTICSEARCH.getType().equalsIgnoreCase(type)) {
                sync = new EsToEsSync(prop);
            } else {
                String error = "同步类型有误，sync.type为" + type + "，sync" +
                               ".type的值只能为:mysql-to-elasticsearch或elasticsearch-to-elasticsearch";
                LOGGER.error(error);
                throw new IllegalArgumentException(error);
            }
            sync.syncData();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

}
