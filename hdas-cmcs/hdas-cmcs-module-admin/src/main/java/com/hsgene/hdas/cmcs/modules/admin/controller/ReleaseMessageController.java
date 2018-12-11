package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsgene.hdas.cmcs.modules.admin.domain.Item;
import com.hsgene.hdas.cmcs.modules.admin.service.IAppService;
import com.hsgene.hdas.cmcs.modules.admin.service.IItemAppService;
import com.hsgene.hdas.cmcs.modules.admin.service.IItemService;
import com.hsgene.hdas.cmcs.modules.admin.service.RedisService;
import com.hsgene.hdas.cmcs.modules.admin.wrapper.DeferredResultWrapper;
import com.hsgene.hdas.cmcs.modules.common.util.DateUtil;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @description: 发布配置信息控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/7/13 10:33
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@RestController
@RequestMapping(value = "/services")
public class ReleaseMessageController {

    @Autowired
    IAppService appService;

    @Autowired
    IItemAppService itemAppService;

    @Autowired
    IItemService itemService;

    @Autowired
    RedisService redisService;

    /**
     * @param appId          app的Id
     * @param appIp          连接app的Ip
     * @param appName        连接app的名字
     * @param notificationId 通知id
     * @return org.springframework.web.context.request.async.DeferredResult<org.springframework.http
     * .ResponseEntity<com.alibaba.fastjson.JSONObject>>
     * @description
     * @author maodi
     * @createDate 2018/7/18 9:03
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/config")
    public ResponseEntity<JSONObject> getConfig(
            @RequestParam(value = "appId", defaultValue = "-1") long appId,
            @RequestParam(value = "appIp", defaultValue = "") String appIp,
            @RequestParam(value = "appName", defaultValue = "") String appName,
            @RequestParam(value = "notificationId", required = false) String notificationId) {
        ResponseEntity<JSONObject> responseEntity = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        List<Future<String>> futures = new ArrayList<Future<String>>();
        //使用多线程是为了能够异步setResult，查询到结果能够及时返回
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1));
        ConfigRelease cr = new ConfigRelease(appId, appIp, notificationId);
        Future<String> future = pool.submit(cr);
        futures.add(future);
        pool.shutdown();
        for (Future<String> f : futures) {
            try {
                if (!f.get().startsWith("{")) {
                    responseEntity = new ResponseEntity<>(HttpStatus.valueOf(Integer.valueOf(f.get())));
                } else {
                    responseEntity = new ResponseEntity<>(JSONObject.parseObject(f.get()), HttpStatus.OK);
                }
            } catch (InterruptedException e) {
                log.error("线程中断出错", e);
            } catch (ExecutionException e) {
                log.error("执行过程中出错", e);
            }
        }
        return responseEntity;
    }

    class ConfigRelease implements Callable {

        /**
         * 间隔查询数据库时间（毫秒）
         */
        private final static long SLEEP_TIME = 500;

        /**
         * 灰度发布超时时间（毫秒）
         */
        private final static long GRAY_TIMEOUT = 60000;
        private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock w = lock.writeLock();

        private Map<Object, Object> itemAppGrayTimeMap;

        long appId;
        String appIp;
        String notificationId;

        ConfigRelease(long appId, String appIp, String notificationId) {
            this.appId = appId;
            this.appIp = appIp;
            this.notificationId = notificationId;
        }

        @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
                Exception.class)
        @Override
        public Object call() {
            //item灰度验证的时间，item和app共同确定时间
            String itemGrayTimeMapAppId = "itemGrayTimeMap_" + appId;
            String lastTimeMapAppId = "lastTimeMap_" + appId;
            String lastAppItemMapAppId = "lastAppItemMap_" + appId;
            String lastAppItemStatusMapAppId = "lastAppItemStatusMap_" + appId;
            if (notificationId == null) {
                //第一次清空app的信息
//                redisService.remove(itemGrayTimeMapAppId);
                redisService.remove(lastTimeMapAppId);
                redisService.remove(lastAppItemMapAppId);
                redisService.remove(lastAppItemStatusMapAppId);
            }
            itemAppGrayTimeMap = new HashMap<>();
            if (redisService.getAll(itemGrayTimeMapAppId) != null && redisService.getAll(itemGrayTimeMapAppId).size() >
                                                                     0) {
                itemAppGrayTimeMap = redisService.getAll(itemGrayTimeMapAppId);
            }
            long startTime = System.currentTimeMillis();
            HttpStatus status = HttpStatus.NOT_MODIFIED;
            while (System.currentTimeMillis() - startTime < DeferredResultWrapper.TIMEOUT) {
                JSONObject notificationJson = new JSONObject();
                Map<String, Object> appIdMap = new HashMap<>(16);
                appIdMap.put("appId", appId);
                try {
                    Map<String, Object> countAppMap = new HashMap<>(16);
                    countAppMap.put("id", appId);
                    countAppMap.put("ip", appIp);
                    long[] itemIds;
                    long[] allItemIds = itemAppService.getItemIdsByAppId(appIdMap);
                    //第一次将当前使用的拿出
                    if (notificationId == null) {
                        appIdMap.put("isDelete", "0");
                        itemIds = itemAppService.getItemIdsByAppId(appIdMap);
                    }
                    //第一次过后将所有的拿出来
                    else {
                        itemIds = itemAppService.getItemIdsByAppId(appIdMap);
                    }
                    long time = 0;
                    //当前app没有注册到数据库中
                    if (itemIds.length < 1) {
                        redisService.set(lastTimeMapAppId, time);
                        continue;
                    }
                    //获取allItemIds中的最新时间
                    time = itemService.getLastUpdateTimeByIds(allItemIds).getTime();
                    long lastTime = Long.valueOf((redisService.get(lastTimeMapAppId) == null) ? "0" : redisService.get
                            (lastTimeMapAppId).toString());
                    if (time == 0) {
                        status = HttpStatus.REQUEST_TIMEOUT;
                    } else {
                        //第一次获取notificationId为空，则不返回
                        boolean flag = notificationId != null && lastTime == time;
                        List<Item> items;
                        //获取的时候状态为2的情况就提前进行状态的修改，然后让后面能够获取到配置信息
                        if (notificationId == null) {
                            lastTime = 0;
                            //isDelete为0是第一次将删除的不进行查出来
                            items = itemService.getByIdsAndIsDeleteAndLastUpdateTime(itemIds, DateUtil
                                    .timestamp2Date(0, DATE_FORMAT), "0");
                        } else {
                            items = itemService.getByIdsAndIsDeleteAndLastUpdateTime(itemIds, DateUtil
                                    .timestamp2Date(time, DATE_FORMAT), null);
                        }
                        for (Item item : items) {
                            long itemId = item.getId();
                            String versionNum = item.getVersionNum();
                            String versionNumBack = versionNum.substring(versionNum.length() - 16, versionNum.length());
                            String versionNumBackAppId = versionNumBack + "_" + appId;
                            long grayTime = 0;
                            if (itemAppGrayTimeMap.get(versionNumBackAppId) != null) {
                                grayTime = Long.valueOf(itemAppGrayTimeMap.get(versionNumBackAppId).toString());
                            }
                            int versionId = item.getVersionId();
                            //判断是否为灰度发布
                            if (versionId == 1) {
                                int releaseStatus = item.getReleaseStatus();
                                //状态为2，进行状态修改
                                if (releaseStatus == 2) {
                                    long[] tempItemIds = {itemId};
                                    boolean status2Flag = releaseStatus == 2 && itemAppGrayTimeMap.get
                                            (versionNumBackAppId) != null && System.currentTimeMillis() - grayTime >
                                                                             GRAY_TIMEOUT;
                                    //该item的第一次进来，直接以灰度状态2启动的，让配置能够获取到是改变了
                                    if (redisService.get(versionNumBack) == null || status2Flag) {
                                        Timestamp lastUpdateTime = StringUtil.getNowTimestamp();
                                        time = lastUpdateTime.getTime();
                                        itemService.updateToGrayReleased(tempItemIds, lastUpdateTime);
                                    }
                                }
                            }
                        }
                        //第一次进来应该is_delete=0
                        if (flag) {
                            status = HttpStatus.NOT_MODIFIED;
                        } else if (appService.countByMap(countAppMap) > 0) {
                            status = HttpStatus.CONFLICT;
                        } else {
                            status = HttpStatus.NOT_MODIFIED;
                            String formatDate = DateUtil.timestamp2Date(lastTime, DATE_FORMAT);
                            if (notificationId == null) {
                                //isDelete为0是第一次将删除的和覆盖的都不进行查出来
                                items = itemService.getByIdsAndIsDeleteAndLastUpdateTime(itemIds, formatDate, "0");
                            } else {
                                //将发布状态所有都查出来
                                items = itemService.getByIdsAndIsDeleteAndLastUpdateTime(itemIds, formatDate, null);
                            }
                            for (Item item : items) {
                                long itemId = item.getId();
                                String versionNum = item.getVersionNum();
                                String versionNumBack = versionNum.substring(versionNum.length() - 16, versionNum
                                        .length());
                                int releaseStatus = item.getReleaseStatus();
                                int isDelete = item.getIsDelete();
                                int versionId = item.getVersionId();
                                String versionNumBackAppId = versionNumBack + "_" + appId;
                                //判断是否为灰度发布
                                if (versionId == 1) {
                                    //灰度发布状态为1
                                    if (releaseStatus == 1) {
                                        //获取锁，没有获取到就阻塞
                                        w.lock();
                                        boolean isLock = true;
                                        //判断是否该item已经灰度状态中
                                        if (redisService.get(versionNumBack) == null || itemAppGrayTimeMap.get
                                                (versionNumBackAppId) != null) {
                                            if (redisService.get(versionNumBack) == null) {
                                                //记录此次发布的app、item以及发布的时间
                                                try {
                                                    redisService.set(versionNumBack, appId);
                                                } finally {
                                                    //释放锁
                                                    w.unlock();
                                                    isLock = false;
                                                }
                                                itemAppGrayTimeMap.put(versionNumBackAppId, System.currentTimeMillis());
                                                if (isDelete == 0) {
                                                    long[] tempItemIds = {itemId};
                                                    itemService.updateToGrayReleasing(tempItemIds);
                                                }
                                            }
                                            notificationDeal(item, notificationJson, appId, time);
                                            status = HttpStatus.OK;
                                        }
                                        if (isLock) {
                                            w.unlock();
                                        }
                                    }
                                    //灰度发布状态为3，则进行在线的推送
                                    else if (releaseStatus == 3) {
                                        notificationDeal(item, notificationJson, appId, time);
                                        status = HttpStatus.OK;
                                    }
                                }
                                //主版本就直接返回配置信息
                                else {
                                    notificationDeal(item, notificationJson, appId, time);
                                    status = HttpStatus.OK;
                                }
                            }
                        }
                        //timestamp为0是要将所有的都查出来，然后筛选为2状态的，防止2状态的被修改过后覆盖了
                        if (notificationId == null) {
                            //isDelete为0是第一次将删除的不进行查出来
                            items = itemService.getByIdsAndIsDeleteAndLastUpdateTime(itemIds, DateUtil
                                    .timestamp2Date(0, DATE_FORMAT), "0");
                        } else {
                            //超过灰度时间才发布修改，在最后，以免在前面获取到
                            items = itemService.getByIdsAndIsDeleteAndLastUpdateTime(itemIds, DateUtil
                                    .timestamp2Date(0, DATE_FORMAT), null);
                        }
                        for (Item item : items) {
                            long itemId = item.getId();
                            String versionNum = item.getVersionNum();
                            String versionNumBack = versionNum.substring(versionNum.length() - 16, versionNum.length());
                            int versionId = item.getVersionId();
                            //判断是否为灰度发布
                            if (versionId == 1) {
                                int releaseStatus = item.getReleaseStatus();
                                //状态为2，进行状态修改
                                if (releaseStatus == 2) {
                                    long[] tempItemIds = {itemId};
                                    String versionNumBackAppId = versionNumBack + "_" + appId;
                                    //修改最后修改时间为当前时间，当前app则保存为当前时间，防止当前的app再次获取，已经在此次中发布过了的
                                    if (itemAppGrayTimeMap.containsKey(versionNumBackAppId)) {
                                        long grayTime = Long.valueOf(itemAppGrayTimeMap.get(versionNumBackAppId)
                                                .toString());
                                        //判断现在时间和第一次灰度发布的时间是否大于灰度时间阈值，大于就修改状态
                                        if (System.currentTimeMillis() - grayTime > GRAY_TIMEOUT) {
                                            Timestamp lastUpdateTime = StringUtil.getNowTimestamp();
                                            time = lastUpdateTime.getTime();
                                            itemService.updateToGrayReleased(tempItemIds, lastUpdateTime);
                                            itemAppGrayTimeMap.remove(versionNumBackAppId);
                                            redisService.remove(versionNumBack);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    redisService.set(lastTimeMapAppId, time);
                } catch (Exception e) {
                    log.error("app获取配置信息出错", e);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                }
                //lastAppItem只保存当前使用的
                appIdMap.put("isDelete", "0");
                redisService.remove(lastAppItemMapAppId);
                String[] itemIdStrs = StringUtil.longArrayAsStringArray(itemAppService.getItemIdsByAppId(appIdMap));
                if (itemIdStrs.length > 0) {
                    redisService.addAll(lastAppItemMapAppId, itemIdStrs);
                }
                redisService.putAll(itemGrayTimeMapAppId, itemAppGrayTimeMap);
                if (!CollectionUtils.isEmpty(notificationJson)) {
                    return notificationJson.toString();
                }
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error("sleep中断", e);
                }
            }
            return String.valueOf(status.value());
        }

        /**
         * @param item             条目实体
         * @param notificationJson 获取到的json数据
         * @param appId            app的id
         * @param time             时间戳
         * @return void
         * @description 将查询的数据根据历史操作跟现在操作进行对比得出现在的操作
         * @author maodi
         * @createDate 2018/9/7 16:05
         */
        private void notificationDeal(Item item, JSONObject notificationJson, long appId, long time) {
            String key = item.getKey();
            String value = item.getValue();
            List<String> parameterList = StringUtil.getParameterList(value);
            long itemInstanceId = item.getInstanceId();
            int itemVersionId = item.getVersionId();
            int itemInstanceTypeId = item.getInstanceTypeId();
            long itemEnvId = item.getEnvId();
            for (String parameter : parameterList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("key", parameter);
                map.put("versionId", itemVersionId);
                if (parameter.startsWith("pub.")) {
                    map.put("instanceId", 0);
                    map.put("isPublic", 1);
                    map.put("envId", itemEnvId);
                } else {
                    map.put("instanceId", itemInstanceId);
                    map.put("isPublic", 0);
                    map.put("envId", 0);
                }
                map.put("instanceTypeId", itemInstanceTypeId);
                String replaceValue = itemService.getValueByMap(map);
                value = value.replace("【" + parameter + "】", replaceValue);
            }
            String versionNum = item.getVersionNum();
            String versionNumBack = versionNum.substring(versionNum.length() - 16, versionNum.length());
            long itemId = item.getId();
            int isDelete = item.getIsDelete();
            int releaseStatus = item.getReleaseStatus();
            Item lastItem = null;
            String lastAppItemStatusMapAppId = "lastAppItemStatusMap_" + appId;
            String lastAppItemMapAppId = "lastAppItemMap_" + appId;
            //保存上次item状态内容
            Map<Object, Object> itemMap = new HashMap<>(16);
            if (redisService.getAll(lastAppItemStatusMapAppId) != null && redisService.getAll(lastAppItemStatusMapAppId)
                                                                                  .size() > 0) {
                lastItem = (Item) redisService.getAll(lastAppItemStatusMapAppId).get(versionNumBack);
                itemMap = redisService.getAll(lastAppItemStatusMapAppId);
            }
            itemMap.put(versionNumBack, item);
            redisService.putAll(lastAppItemStatusMapAppId, itemMap);
            int lastReleaseStatus = 0;
            int lastIsDelete = 0;
            if (lastItem != null) {
                lastReleaseStatus = lastItem.getReleaseStatus();
                lastIsDelete = lastItem.getIsDelete();
            }
            Object arrayObj = redisService.getSet(lastAppItemMapAppId);
            Set<String> appItemIdSet = new HashSet<>();
            if (arrayObj != null) {
                appItemIdSet = (Set<String>) arrayObj;
            }
            String type = null;
            boolean releaseStatus0 = releaseStatus == 0;
            boolean releaseStatus1or3 = releaseStatus == 1 || releaseStatus == 3;
            boolean releaseStatus1or2or3 = releaseStatus == 1 || releaseStatus == 2 || releaseStatus == 3;
            boolean isDelete0 = isDelete == 0;
            boolean isDelete1or2 = isDelete == 1 || isDelete == 2;
            boolean lastReleaseStatus0orNull = lastReleaseStatus == 0 || lastItem == null;
            boolean lastReleaseStatus1or2or3 = lastReleaseStatus == 1 || lastReleaseStatus == 2 || lastReleaseStatus
                                                                                                   == 3;
            boolean lastIsDelete0orNull = lastIsDelete == 0 || lastItem == null;
            boolean lastIsDelete0or2 = lastIsDelete == 0 || lastIsDelete == 2;
            boolean lastIsDelete0 = lastIsDelete == 0;
            //第一次进来
            if (arrayObj == null && releaseStatus1or3 && isDelete0) {
                type = "新增";
            }
            String itemIdStr = String.valueOf(itemId);
            //取消item_app时没有消息
            if (arrayObj != null) {
                if (releaseStatus1or3 && isDelete0 && lastReleaseStatus0orNull && lastIsDelete0orNull) {
                    type = "新增";
                }
                if (releaseStatus1or3 && isDelete0 && lastReleaseStatus0orNull && lastIsDelete0orNull && !appItemIdSet
                        .contains(itemIdStr)) {
                    type = "新增";
                }
                if (releaseStatus1or3 && isDelete0 && lastReleaseStatus1or2or3 && lastIsDelete0or2 && appItemIdSet
                        .contains(itemIdStr)) {
                    type = "修改";
                }
                if (releaseStatus1or2or3 && isDelete0 && lastReleaseStatus1or2or3 && lastIsDelete0or2 && !appItemIdSet
                        .contains(itemIdStr)) {
                    type = "新增";
                }
                if (releaseStatus0 && isDelete0 && lastReleaseStatus1or2or3 && lastIsDelete0 && appItemIdSet.contains
                        (itemIdStr)) {
                    type = "删除";
                }
                if (releaseStatus1or2or3 && isDelete1or2 && lastReleaseStatus1or2or3) {
                    type = "删除";
                }
                /*System.out.println(type + " " + releaseStatus + "    " + isDelete + "    " + lastReleaseStatus + "
                 " + lastIsDelete + "  " + appItemIdSet);*/
            }
            if (type == null) {
                return;
            }
            //如果type为删除，lastAppItemStatusMap.remove;
            if (redisService.getAll(lastAppItemStatusMapAppId) != null && redisService.getAll(lastAppItemStatusMapAppId)
                                                                                  .size() > 0 && "删除".equals(type)) {
                redisService.getAll(lastAppItemStatusMapAppId).remove(versionNumBack);
            }
            String notificationId = StringUtil.getNotificationId(appId, time);
            String instanceId = String.valueOf(item.getInstanceId());
            if (notificationJson.containsKey(notificationId)) {
                JSONObject joBynId = (JSONObject) notificationJson.get(notificationId);
                if (joBynId.containsKey(instanceId)) {
                    JSONArray jaByiId = joBynId.getJSONArray(instanceId);
                    JSONObject joByiId = new JSONObject();
                    joByiId.put("key", key);
                    joByiId.put("value", value);
                    joByiId.put("type", type);
                    jaByiId.add(joByiId);
                } else {
                    JSONArray jaByiId = new JSONArray();
                    JSONObject joByiId = new JSONObject();
                    joByiId.put("key", key);
                    joByiId.put("value", value);
                    joByiId.put("type", type);
                    jaByiId.add(joByiId);
                    joBynId.put(instanceId, jaByiId);
                }

            } else {
                JSONObject joBynId = new JSONObject();
                JSONArray jaByiId = new JSONArray();
                JSONObject joByiId = new JSONObject();
                joByiId.put("key", key);
                joByiId.put("value", value);
                joByiId.put("type", type);
                jaByiId.add(joByiId);
                joBynId.put(instanceId, jaByiId);
                notificationJson.put(notificationId, joBynId);
            }
            //如果先删除，再新增则认为是修改
            JSONObject jsonObject1 = (JSONObject) notificationJson.get(notificationId);
            Set<String> keySet1 = jsonObject1.keySet();
            for (String key1 : keySet1) {
                JSONArray jsonArray1 = jsonObject1.getJSONArray(key1);
                JSONArray jsonArray2 = new JSONArray();
                if (jsonArray1.size() == 1) {
                    jsonArray2.add(jsonArray1.getJSONObject(0));
                } else {
                    JSONObject deleteJsonObject = new JSONObject();
                    for (int i = 0, length = jsonArray1.size(); i < length; i++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                        String tempType = jsonObject2.getString("type");
                        String tempKey = jsonObject2.getString("key");
                        if ("删除".equals(tempType)) {
                            deleteJsonObject.put(tempKey, jsonObject2);
                        }
                    }
                    Set<String> deleteJsonKeySet = deleteJsonObject.keySet();
                    for (int i = 0, length = jsonArray1.size(); i < length; i++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                        String tempType = jsonObject2.getString("type");
                        String tempKey = jsonObject2.getString("key");
                        if (("新增".equals(tempType) || "修改".equals(tempType)) && deleteJsonKeySet.contains(tempKey)) {
                            jsonObject2.put("type", "修改");
                            jsonArray2.add(jsonObject2);
                            deleteJsonObject.remove(tempKey);
                        } else if (!"删除".equals(tempType)) {
                            jsonArray2.add(jsonObject2);
                        }
                    }
                    deleteJsonKeySet = deleteJsonObject.keySet();
                    for (String deleteKey : deleteJsonKeySet) {
                        jsonArray2.add(deleteJsonObject.getJSONObject(deleteKey));
                    }
                }
                jsonObject1.put(key1, jsonArray2);
            }
        }
    }

}