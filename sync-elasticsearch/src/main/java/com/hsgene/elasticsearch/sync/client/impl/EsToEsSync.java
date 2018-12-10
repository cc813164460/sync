package com.hsgene.elasticsearch.sync.client.impl;

import com.hsgene.elasticsearch.sync.client.SyncData;
import com.hsgene.elasticsearch.sync.domain.PoolNumAndEverySize;
import com.hsgene.elasticsearch.sync.util.ElasticSearchPool;
import com.hsgene.elasticsearch.sync.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @description: elasticsearch同步数据到elasticsearch
 * @projectName: sync_elasticsearch
 * @package: sync.client
 * @author: maodi
 * @createDate: 2018/12/3 16:13
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class EsToEsSync implements SyncData {

    private final static Logger LOGGER = LogManager.getLogger(EsToEsSync.class);

    /**
     * elasticsearch源集群节点集配置名
     */
    private final static String SOURCE_CLUSTER_NODES = "elasticsearch.source.cluster-nodes";

    /**
     * elasticsearch源集群名称配置名
     */
    private final static String SOURCE_CLUSTER_NAME = "elasticsearch.source.cluster-name";

    /**
     * elasticsearch源索引配置名
     */
    private final static String SOURCE_INDEX = "elasticsearch.source.index";

    /**
     * elasticsearch源类型名称配置名
     */
    private final static String SOURCE_TYPE_NAME = "elasticsearch.source.type-name";

    /**
     * elasticsearch目标集群节点集配置名
     */
    private final static String TARGET_CLUSTER_NODES = "elasticsearch.target.cluster-nodes";

    /**
     * elasticsearch目标集群名称配置名
     */
    private final static String TARGET_CLUSTER_NAME = "elasticsearch.target.cluster-name";

    /**
     * elasticsearch目标索引配置名
     */
    private final static String TARGET_INDEX = "elasticsearch.target.index";

    /**
     * elasticsearch目标类型名称配置名
     */
    private final static String TARGET_TYPE_NAME = "elasticsearch.target.type-name";

    /**
     * 源elasticsearch集群名称
     */
    private static String sourceClusterName;

    /**
     * 源elasticsearch索引名称
     */
    private static String sourceIndex;

    /**
     * 源elasticsearch类型名称
     */
    private static String sourceTypeName;

    /**
     * 目标elasticsearch名称
     */
    private static String targetClusterName;

    /**
     * 目标elasticsearch索引名称
     */
    private static String targetIndex;

    /**
     * 目标elasticsearch类型名称
     */
    private static String targetTypeName;

    /**
     * 源集群节点
     */
    private static List<String> sourceClusterNodes;

    /**
     * 目标集群节点
     */
    private static List<String> targetClusterNodes;

    public EsToEsSync(Properties prop) {
        try {
            sourceClusterNodes = Arrays.asList(prop.getProperty(SOURCE_CLUSTER_NODES).split(","));
            sourceClusterName = prop.getProperty(SOURCE_CLUSTER_NAME);
            sourceIndex = prop.getProperty(SOURCE_INDEX);
            sourceTypeName = prop.getProperty(SOURCE_TYPE_NAME);
            LOGGER.info("获取elasticsearch源配置参数成功");
        } catch (Exception e) {
            LOGGER.error("获取elasticsearch源配置参数出错", e);
            e.printStackTrace();
        }
        try {
            targetClusterNodes = Arrays.asList(prop.getProperty(TARGET_CLUSTER_NODES).split(","));
            targetClusterName = prop.getProperty(TARGET_CLUSTER_NAME);
            targetIndex = prop.getProperty(TARGET_INDEX);
            targetTypeName = prop.getProperty(TARGET_TYPE_NAME);
            LOGGER.info("获取elasticsearch目标配置参数成功");
        } catch (Exception e) {
            LOGGER.error("获取elasticsearch目标配置参数出错", e);
            e.printStackTrace();
        }
    }

    @Override
    public void syncData() {
        try {
            LOGGER.info("开始从" + sourceClusterName + "的" + sourceIndex + "索引同步数据到" + targetClusterName + "的" +
                        targetIndex + "索引");
            LOGGER.info("开始初始化源elasticsearch连接池...");
            ElasticSearchPool sourcePool = new ElasticSearchPool(sourceClusterName, sourceClusterNodes);
            LOGGER.info("初始化源elasticsearch连接池完成");
            LOGGER.info("开始初始化目标elasticsearch连接池...");
            ElasticSearchPool targetPool = new ElasticSearchPool(targetClusterName, targetClusterNodes);
            LOGGER.info("初始化目标elasticsearch连接池完成");
            TransportClient client = sourcePool.getResource();
            long startTime = System.currentTimeMillis();
            SearchRequestBuilder searchBuilder = client.prepareSearch(sourceIndex).setTypes(sourceTypeName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            SearchResponse response = searchBuilder.execute().actionGet();
            RestStatus status = response.status();
            if (status.getStatus() != RestStatus.OK.getStatus()) {
                LOGGER.error(status.name());
            }
            SearchHits searchHits = response.getHits();
            int totalCount = (int) searchHits.getTotalHits();
            LOGGER.info("源elasticsearch数据总共数量为：" + totalCount);
            PoolNumAndEverySize poolNumAndEverySize = StringUtil.calcPoolNumAndEverySize(totalCount);
            int everySize = poolNumAndEverySize.getEverySize();
            int poolNum = poolNumAndEverySize.getPoolNum();
            client.close();
            List<Future<Integer>> futures = new ArrayList<>();
            ThreadPoolExecutor pool = new ThreadPoolExecutor(StringUtil.POOL_NUM, StringUtil.POOL_NUM, 0,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(poolNum));
            for (int i = 0; i < poolNum; i++) {
                int from = everySize * i;
                EsToEsSyncEverySend everySend = new EsToEsSyncEverySend(sourceIndex, targetIndex, sourceTypeName,
                        targetTypeName, sourcePool, targetPool, from, everySize);
                Future<Integer> future = pool.submit(everySend);
                futures.add(future);
                everySend = null;
                future = null;
            }
            //停止线程池，不能再加入新的线程
            pool.shutdown();
            //等待线程执行完
            int syncTotalCount = StringUtil.getAllCount(futures);
            LOGGER.info("正在关闭源elasticsearch连接池...");
            sourcePool.destroy();
            LOGGER.info("源elasticsearch连接池关闭成功");
            LOGGER.info("正在关闭目标elasticsearch连接池...");
            targetPool.destroy();
            LOGGER.info("目标elasticsearch连接池关闭成功");
            printSpeed(startTime, syncTotalCount, sourceIndex, targetIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param startTime   开始时间
     * @param totalCount  总共数量
     * @param sourceIndex 源索引名
     * @param targetIndex 目标索引名
     * @return void
     * @description 格式化输出结果
     * @author maodi
     * @createDate 2018/12/4 14:35
     */
    public static void printSpeed(long startTime, long totalCount, String sourceIndex, String targetIndex) {
        float spendTime = (float) (System.currentTimeMillis() - startTime) / 1000;
        float speed = totalCount / spendTime;
        String info = "\n******同步完成******\n源索引：" + sourceIndex + ";\n目标索引：" + targetIndex + ";\n数量：" + totalCount +
                      ";\n" + "时间：" + spendTime + "秒;\n每秒：" + String.format("%.2f", speed) + "条;\n";
        LOGGER.info(info);
    }

}

/**
 * @description: 单独传输的线程
 * @projectName: sync_elasticsearch
 * @package: sync.client
 * @author: maodi
 * @createDate: 2018/12/3 16:13
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
class EsToEsSyncEverySend implements Callable {

    private final static Logger LOGGER = LogManager.getLogger(EsToEsSyncEverySend.class);

    private ElasticSearchPool sourcePool;
    private ElasticSearchPool targetPool;
    private String sourceIndex;
    private String targetIndex;
    private String sourceTypeName;
    private String targetTypeName;
    private int from;
    private int everySize;

    public EsToEsSyncEverySend(String sourceIndex, String targetIndex, String sourceTypeName, String targetTypeName,
                               ElasticSearchPool sourcePool, ElasticSearchPool targetPool, int from, int everySize) {
        this.sourcePool = sourcePool;
        this.targetPool = targetPool;
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        this.sourceTypeName = sourceTypeName;
        this.targetTypeName = targetTypeName;
        this.from = from;
        this.everySize = everySize;
    }

    @Override
    public Object call() {
        int start = from + 1;
        try {
            TransportClient sourceTransportClient = sourcePool.getResource();
            SearchRequestBuilder searchBuilder = sourceTransportClient.prepareSearch(sourceIndex)
                    .setTypes(sourceTypeName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            //按照id排序以免下次查询重复数据
            searchBuilder.addSort(SortBuilders.fieldSort("_id"))
                    //设置游标维持时间，避免没有查询完就返回(使用scroll处理了max_result_window的最大数量的限制)
//                    .setScroll(TimeValue.timeValueMinutes(10))
                    //开始位置
                    .setFrom(from)
                    //查询数量
                    .setSize(everySize)
                    .execute().actionGet();
            SearchResponse searchResponse = searchBuilder.execute().actionGet();
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();
            TransportClient targetTransportClient = targetPool.getResource();
            IndexRequestBuilder builder = targetTransportClient.prepareIndex(targetIndex, targetTypeName);
            for (SearchHit hit : hits) {
                String id = hit.getId();
                String json = hit.getSourceAsString();
                if (StringUtils.isBlank(json)) {
                    LOGGER.warn("id为[" + id + "]数据为空[" + json + "]，同步程序继续运行");
                    continue;
                }
                String targetJson = json;
                try {
                    targetJson = StringUtil.sourceJsonToTargetJson(json);
                } catch (Exception e) {
                    LOGGER.warn("将源数据改造为目标数据格式有误，有误数据id为" + id + "，同步程序继续运行", e);
                }
                IndexResponse indexResponse = builder.setId(id).setSource(targetJson, XContentType.JSON).execute()
                        .actionGet();
                RestStatus restStatus = indexResponse.status();
                int status = restStatus.getStatus();
                if (status != RestStatus.CREATED.getStatus() && status != RestStatus.OK.getStatus()) {
                    LOGGER.warn("同步数据有误，同步操作状态为" + restStatus.name() + "，有误数据id为" + id + "，同步程序继续运行");
                }
                hit = null;
                id = null;
                json = null;
                targetJson = null;
                indexResponse = null;
                restStatus = null;
            }
            int hitsLength = hits.length;
            int end = from + hitsLength;
            searchBuilder = null;
            searchResponse = null;
            searchHits = null;
            hits = null;
            builder = null;
            //归还源elasticsearch连接
            sourcePool.returnResource(sourceTransportClient);
            //归还目标elasticsearch连接
            targetPool.returnResource(targetTransportClient);
            LOGGER.info("******同步完第" + start + "至" + end + "条数据******");
            return hitsLength;
        } catch (Exception e) {
            LOGGER.error("同步第" + start + "条到第" + (from + everySize) + "条数据时出错", e);
            return 0;
        }
    }

}
