package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.hsgene.hdas.cmcs.modules.admin.domain.*;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.*;
import com.hsgene.hdas.cmcs.modules.admin.util.OrderedProperties;
import com.hsgene.hdas.cmcs.modules.admin.util.PageInfoUtil;
import com.hsgene.hdas.cmcs.modules.common.util.DateUtil;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @description: 公共配置控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/public_config")
public class PublicConfigController {

    @Autowired
    private IPublicConfigService publicConfigService;

    @Autowired
    private IInstanceService instanceService;

    @Autowired
    private IInstanceTypeService instanceTypeService;

    @Autowired
    private IItemService itemService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IItemAppService itemAppService;

    @Autowired
    private IItemResourceService itemResourceService;

    @Autowired
    private IRoleClassService roleClassService;

    /**
     * @param hsr      http请求
     * @param username 用户名
     * @return java.lang.Object 公共配置properties分页数据
     * @description 获取实例properties分页数据
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping("/query_properties")
    public @ResponseBody
    Object getQueryProperties(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            if ("admin".equals(username)) {
                maxClassId = 2;
            }
            long versionId = Long.valueOf(hsr.getParameter("versionId"));
            Map<String, Object> map = new HashMap<>(16);
            map.put("versionId", versionId);
            return publicConfigService.selectPropertiesByPage(hsr, maxClassId);
        } catch (Exception e) {
            log.error("获取实例properties分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取实例properties分页数据出错");
        }
    }

    /**
     * @param hsr http请求
     * @return java.lang.Object 除properties外公共配置的数据
     * @description 获取除properties外配置的数据
     * @author maodi
     * @createDate 2018/7/5 17:11
     */
    @RequestMapping("/query_exclude_properties")
    public @ResponseBody
    Object getQueryExcludeProperties(HttpServletRequest hsr) {
        try {
            long instanceTypeId = Long.valueOf(hsr.getParameter("instanceTypeId") == null ? "-1" : hsr.getParameter
                    ("instanceTypeId"));
            long versionId = Long.valueOf(hsr.getParameter("versionId") == null ? "-1" : hsr.getParameter("versionId"));
            long envId = Long.valueOf(hsr.getParameter("envId") == null ? "-1" : hsr.getParameter("envId"));
            Map<String, Object> map = new HashMap<>(16);
            if (instanceTypeId != -1) {
                map.put("instanceTypeId", instanceTypeId);
            }
            if (versionId != -1) {
                map.put("versionId", versionId);
            }
            if (envId != -1) {
                map.put("envId", envId);
            }
            List<Item> list = itemService.getPublicItemByInstanceTypeIdAndVersionId(map);
            //美化json数据
            if (instanceTypeId == 1) {
                for (Item item : list) {
                    Object obj = item.getValue();
                    if (obj != null) {
                        item.setValue(StringUtil.formatJSONFile(obj.toString()));
                    }
                }
            }
            return list;
        } catch (Exception e) {
            log.error("获取公共配置实例其他配置数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取公共配置实例其他配置数据出错");
        }
    }

    /**
     * @param hsr http请求
     * @return java.lang.Object
     * @description 获取项目模块列表信息
     * @author maodi
     * @createDate 2018/7/23 14:24
     */
    @RequestMapping("/get_product_module_list")
    public @ResponseBody
    Object getProductModuleEnvList(HttpServletRequest hsr) {
        try {
            int draw = Integer.valueOf(hsr.getParameter("draw") == null ? "1" : hsr.getParameter("draw"));
            long versionId = Long.valueOf(hsr.getParameter("versionId") == null ? "-1" : hsr.getParameter("versionId"));
            long itemId = Long.valueOf(hsr.getParameter("itemId") == null ? "-1" : hsr.getParameter("itemId"));
            long moduleId = Long.valueOf(hsr.getParameter("moduleId") == null ? "-1" : hsr.getParameter("moduleId"));
            long envId = Long.valueOf(hsr.getParameter("envId") == null ? "-1" : hsr.getParameter("envId"));
            String name = hsr.getParameter("name");
            long configId = Long.valueOf(hsr.getParameter("configId") == null ? "-1" : hsr.getParameter("configId"));
            Map<String, Object> map = new HashMap<>(16);
            map.put("versionId", versionId);
            map.put("itemId", itemId);
            map.put("envId", envId);
            if (StringUtils.isNotBlank(name)) {
                map.put("name", name);
            }
            if (moduleId != -1) {
                map.put("moduleId", moduleId);
            }
            if (configId != -1) {
                map.put("configId", configId);
            }
            List<Map<String, Object>> listMap = itemResourceService.getProductModuleEnvList(map);
            return PageInfoUtil.dealListMap(listMap, draw);
        } catch (Exception e) {
            log.error("获取公共配置实例项目模块数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取公共配置实例项目模块数据出错");
        }
    }

    /**
     * @param json     实例数据
     * @param username 用户名
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 新增结果
     * @description 新增公共配置信息
     * @author maodi
     * @createDate 2018/6/28 17:26
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(@RequestBody JSONObject json, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            InstanceInfo instanceInfo = json.toJavaObject(InstanceInfo.class);
            int instanceTypeId = instanceInfo.getInstanceTypeId();
            String comment = instanceInfo.getComment();
            String key = instanceInfo.getKey();
            String value = instanceInfo.getValue();
            boolean isValid = false;
            String fileType = "";
            if (instanceTypeId == 0) {
                isValid = true;
            } else if (instanceTypeId == 1) {
                isValid = StringUtil.validJSONFile(value);
                fileType = "json";
            } else if (instanceTypeId == 2) {
                isValid = StringUtil.validXmlFile(value);
                fileType = "xml";
            } else if (instanceTypeId == 3) {
                isValid = StringUtil.validYmlFile(value);
                fileType = "yml";
            }
            if (!isValid) {
                return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, fileType + "文件内容格式不对");
            }
            long[] appIds = instanceInfo.getAppIds();
            long[] resourceIds = getSelectResourceIdsByListMap(instanceInfo.getResourceIdsListMap());
            Item item = new Item();
            long itemId = StringUtil.getId();
            if (instanceInfo.getItemId() != -1) {
                itemId = instanceInfo.getItemId();
            }
            item.setId(itemId);
            item.setComment(comment);
            item.setKey(key);
            item.setValue(value);
            item.setLastUpdateBy(userService.getIdByName(username));
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            item.setLastUpdateTime(timestamp);
            item.setUpdateTime(timestamp);
            int versionId = instanceInfo.getVersionId();
            item.setVersionId(versionId);
            item.setReleaseStatus(0);
            item.setLineNum(instanceInfo.getLineNum());
            item.setIsPublic(1);
            item.setInstanceTypeId(instanceTypeId);
            item.setVersionNum(instanceInfo.getVersionNum() == null ? StringUtil.getVersionNum(0, versionId, 1, 0) :
                    instanceInfo.getVersionNum());
            item.setClassId(instanceInfo.getClassId());
            long envId = instanceInfo.getEnvId();
            item.setEnvId(envId);
            item.setMustChange(instanceInfo.getMustChange());
            Map<String, Object> itemCountMap = new HashMap<>(16);
            itemCountMap.put("key", key);
            itemCountMap.put("instanceTypeId", instanceTypeId);
            itemCountMap.put("versionId", versionId);
            itemCountMap.put("envId", envId);
            long[] publicApplyResourceIds = instanceInfo.getResourceIds();
            long[] instanceIds = instanceService.getInstanceIdsByResourceIds(publicApplyResourceIds);
            if (instanceIds != null && instanceIds.length > 0) {
                itemCountMap.put("instanceIds", instanceIds);
            }
            int itemCount = itemService.countByMap(itemCountMap);
            List<String> mainKeyList = new ArrayList<>();
            if (versionId == 1) {
                String[] keys = itemService.getPublicMainKeysByInstanceTypeId(instanceTypeId);
                if (keys.length > 0) {
                    mainKeyList = Arrays.asList(keys);
                } else {
                    return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                }
            }
            if (itemCount < 1) {
                if (versionId == 0) {
                    itemService.save(item);
                } else {
                    if (mainKeyList.contains(key)) {
                        itemService.save(item);
                    } else {
                        return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                    }
                }
            } else {
                return new ConfigResponse(HttpStatus.CONFLICT.value(), 0, getDuplicateInfo(getProductResourceIds
                        (key, itemId, publicApplyResourceIds)));
            }
            itemResourceSave(resourceIds, itemId, instanceTypeId);
            if (appIds != null && appIds.length > 0) {
                itemAppSave(appIds, itemId);
            }
            return new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
        } catch (Exception e) {
            log.error("添加公共配置实例配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "添加公共配置实例配置出错");
        }
    }

    /**
     * @param appIds appId的数组
     * @param itemId 条目id
     * @return void
     * @description 存储条目数据
     * @author maodi
     * @createDate 2018/7/5 17:13
     */
    private void itemAppSave(long[] appIds, long itemId) {
        int appNum = 0;
        for (long appId : appIds) {
            ItemApp itemApp = new ItemApp();
            itemApp.setId(StringUtil.getId() + appNum++);
            itemApp.setAppId(appId);
            itemApp.setItemId(itemId);
            itemAppService.save(itemApp);
        }
    }

    /**
     * @param resourceIds    资源ids
     * @param itemId         条目id
     * @param instanceTypeId 实例类型id
     * @return void
     * @description 保存条目资源信息
     * @author maodi
     * @createDate 2018/7/23 14:22
     */
    private void itemResourceSave(long[] resourceIds, long itemId, int instanceTypeId) {
        int resourceNum = 0;
        for (long resourceId : resourceIds) {
            ItemResource itemResource = new ItemResource();
            itemResource.setId(StringUtil.getId() + resourceNum++);
            itemResource.setResourceId(resourceId);
            itemResource.setItemId(itemId);
            itemResourceService.save(itemResource);
            Map<String, Object> map = new HashMap<>(16);
            map.put("instanceTypeId", instanceTypeId);
            map.put("resourceId", resourceId);
            long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            //如果没有instance则添加项目对应的instance，否则不进行操作
            if (instanceId == -1) {
                Instance instance = new Instance();
                instance.setId(StringUtil.getId());
                instance.setResourceId(String.valueOf(resourceId));
                instance.setInstanceTypeId(instanceTypeId);
                InstanceType instanceType = instanceTypeService.get(instanceTypeId);
                instance.setName(instanceType.getName());
                instanceService.save(instance);
            }
        }
    }

    /**
     * @param json     配置信息
     * @param username 用户名
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 复制结果
     * @description 复制公共配置信息
     * @author maodi
     * @createDate 2018/8/31 14:30
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert_copy_config")
    public @ResponseBody
    ConfigResponse insertCopyConfig(@RequestBody JSONObject json, @SessionAttribute(Constant.SESSION_KEY) String
            username) {
        try {
            InstanceCopyConfigData copyConfigData = json.toJavaObject(InstanceCopyConfigData.class);
            int instanceTypeId = copyConfigData.getInstanceTypeId();
            int versionId = copyConfigData.getVersionId();
            long envId = copyConfigData.getEnvId();
            List<Map<String, Object>> copyConfigList = copyConfigData.getCopyConfigList();
            int lineNum = 0;
            for (Map<String, Object> tempMap : copyConfigList) {
                long itemId = StringUtil.getId();
                String key = tempMap.get("key").toString();
                String value = tempMap.get("value").toString();
                String comment = tempMap.get("comment").toString();
                int classId = Integer.valueOf(tempMap.get("classId").toString());
                String versionNum = tempMap.get("versionNum").toString();
                int mustChange = Integer.valueOf(tempMap.get("mustChange").toString());
                long userId = userService.getIdByName(username);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Item item = new Item();
                item.setId(itemId);
                item.setKey(key);
                item.setValue(value);
                item.setComment(comment);
                item.setLastUpdateBy(userId);
                item.setLastUpdateTime(timestamp);
                item.setUpdateTime(timestamp);
                item.setVersionId(versionId);
                item.setReleaseStatus(0);
                item.setIsPublic(1);
                item.setLineNum(lineNum++);
                item.setInstanceTypeId(instanceTypeId);
                item.setVersionNum(versionNum);
                item.setClassId(classId);
                item.setEnvId(envId);
                item.setMustChange(mustChange);
                Map<String, Object> itemCountMap = new HashMap<>(16);
                itemCountMap.put("key", key);
                itemCountMap.put("instanceTypeId", instanceTypeId);
                itemCountMap.put("versionId", versionId);
                itemCountMap.put("envId", envId);
                int itemCount = itemService.countByMap(itemCountMap);
                List<String> mainKeyList = new ArrayList<>();
                if (versionId == 1) {
                    String[] keys = itemService.getPublicMainKeysByInstanceTypeId(instanceTypeId);
                    if (keys.length > 0) {
                        mainKeyList = Arrays.asList(keys);
                    } else {
                        return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                    }
                }
                if (itemCount < 1) {
                    if (versionId == 0) {
                        itemService.save(item);
                    } else {
                        if (mainKeyList.contains(key)) {
                            itemService.save(item);
                        } else {
                            return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                        }
                    }
                } else {
                    return new ConfigResponse(HttpStatus.CONFLICT.value(), 0, "您复制的内容不能与当前环境公共配置重复");
                }
            }
            return new ConfigResponse(HttpStatus.OK.value(), 1, "复制成功！");
        } catch (Exception e) {
            log.error("新增公共配置实例配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "复制公共配置出错");
        }
    }

    /**
     * @param hsr      http请求
     * @param username 用户名
     * @return java.lang.Object 配置信息
     * @description 获取该条件下所有的配置信息
     * @author maodi
     * @createDate 2018/8/31 14:31
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_all_key_and_value")
    public @ResponseBody
    Object getAllKeyAndValue(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            return publicConfigService.getAllKeyAndValue(hsr, username);
        } catch (Exception e) {
            log.error("获取公共配置properties数据出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取公共配置properties数据出错");
        }
    }

    /**
     * @param hsr      http请求，包含参数
     * @param username 用户名
     * @return java.lang.Object 实例properties分页数据
     * @description 根据key获取实例properties分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByKeyPage(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            if ("admin".equals(username)) {
                maxClassId = 2;
            }
            return publicConfigService.selectPropertiesByKeyPage(hsr, maxClassId);
        } catch (Exception e) {
            log.error("根据key获取公共配置实例properties分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据key获取公共配置实例properties分页数据出错");
        }
    }

    /**
     * @param json     实例数据
     * @param username 用户名
     * @return java.lang.Object 更新结果
     * @description 更新实例数据
     * @author maodi
     * @createDate 2018/6/28 17:27
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(@RequestBody JSONObject json, @SessionAttribute(Constant.SESSION_KEY) String username) {
        ConfigResponse configResponse;
        try {
            InstanceInfo instanceInfo = json.toJavaObject(InstanceInfo.class);
            int instanceTypeId = instanceInfo.getInstanceTypeId();
            long[] appIds = instanceInfo.getAppIds();
            List<Map<String, Object>> resourceIdsListMap = instanceInfo.getResourceIdsListMap();
            long[] resourceIds = getSelectResourceIdsByListMap(resourceIdsListMap);
            String comment = instanceInfo.getComment();
            String key = instanceInfo.getKey();
            String value = instanceInfo.getValue();
            int releaseStatus = instanceInfo.getReleaseStatus();
            boolean isValid = false;
            String fileType = "";
            if (instanceTypeId == 0) {
                isValid = true;
            } else if (instanceTypeId == 1) {
                isValid = StringUtil.validJSONFile(value);
                fileType = "json";
            } else if (instanceTypeId == 2) {
                isValid = StringUtil.validXmlFile(value);
                fileType = "xml";
            } else if (instanceTypeId == 3) {
                isValid = StringUtil.validYmlFile(value);
                fileType = "yml";
            }
            if (!isValid) {
                return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, fileType + "文件内容格式不对");
            }
            long envId = instanceInfo.getEnvId();
            int versionId = instanceInfo.getVersionId();
            Item item = new Item();
            long itemId = instanceInfo.getItemId();
            Map<String, Object> itemCountMap = new HashMap<>(16);
            itemCountMap.put("key", key);
            itemCountMap.put("id", itemId);
            itemCountMap.put("instanceTypeId", instanceTypeId);
            itemCountMap.put("versionId", versionId);
            itemCountMap.put("envId", envId);
            long[] publicApplyResourceIds = instanceInfo.getResourceIds();
            long[] instanceIds = instanceService.getInstanceIdsByResourceIds(publicApplyResourceIds);
            if (instanceIds != null && instanceIds.length > 0) {
                itemCountMap.put("instanceIds", instanceIds);
            }
            int itemCount = itemService.countByMap(itemCountMap);
            if (instanceTypeId != 0) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("key", key);
                map.put("resourceIds", publicApplyResourceIds);
                itemCount = itemService.countProduct(map);
            }
            if (itemCount > 0) {
                return new ConfigResponse(HttpStatus.CONFLICT.value(), 0, getDuplicateInfo(getProductResourceIds
                        (key, itemId, publicApplyResourceIds)));
            }
            long newItemId = StringUtil.getId();
            item.setId(newItemId);
            item.setComment(comment);
            item.setKey(key);
            item.setValue(value);
            item.setLastUpdateBy(userService.getIdByName(username));
            Timestamp timestamp = StringUtil.getNowTimestamp();
            item.setLastUpdateTime(timestamp);
            item.setUpdateTime(timestamp);
            item.setVersionId(versionId);
            item.setIsPublic(1);
            item.setReleaseStatus(releaseStatus);
            item.setLineNum(instanceInfo.getLineNum());
            item.setVersionNum(instanceInfo.getVersionNum());
            item.setInstanceTypeId(instanceTypeId);
            item.setClassId(instanceInfo.getClassId());
            item.setEnvId(envId);
            item.setMustChange(instanceInfo.getMustChange());
            long[] itemIds = {itemId};
            itemService.overByIds(itemIds);
            itemAppService.overByItemIds(itemIds);
            itemResourceService.overByItemIds(itemIds);
            itemService.save(item);
            Map<String, Object> map = new HashMap<>(16);
            map.put("versionId", versionId);
            map.put("itemId", itemId);
            map.put("envId", envId);
            List<Map<String, Object>> listMap = itemResourceService.getProductModuleEnvList(map);
            Set<Long> newAllResourceIdSet = getAllResourceIdsSetByListMap(resourceIdsListMap);
            itemResourceSave(getOldResourceIds(listMap, newAllResourceIdSet), newItemId, instanceTypeId);
            itemResourceSave(resourceIds, newItemId, instanceTypeId);
            if (appIds != null && appIds.length > 0) {
                itemAppSave(appIds, newItemId);
            }
            configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
        } catch (Exception e) {
            log.error("修改公共配置实例配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改公共配置实例配置出错");
        }
        return configResponse;
    }

    /**
     * @param ids 条目id数组
     * @return java.lang.Object 删除结果
     * @description 根据ids删除条目数据以及条目关联app的数据
     * @author maodi
     * @createDate 2018/6/13 15:42
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            List<Item> list = itemService.getByIds(ids);
            List<String> keyList = new ArrayList<>();
            int versionId = 1;
            int instanceTypeId = 0;
            for (int i = 0, length = list.size(); i < length; i++) {
                Item item = list.get(i);
                versionId = item.getVersionId();
                instanceTypeId = item.getInstanceTypeId();
                keyList.add(item.getKey());
            }
            //如果为主版本ids，获取灰度版本对应的ids，删除灰度版本
            if (versionId == 0) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("instanceTypeId", instanceTypeId);
                map.put("keys", keyList);
                long[] grayIds = itemService.getPublicGrayIdsByInstanceTypeIdAndKeys(map);
                //获取版本号，然后删除改版本号的所有
                if (grayIds.length > 0) {
                    String[] versionNumBacks = itemService.getVersionNumBacksByIds(grayIds);
                    long[] useItemIds = itemService.getUseIdsByVersionNumBacks(versionNumBacks);
                    itemService.deleteAndUpdateTimeByIds(useItemIds);
                    long[] itemIds = itemService.getIdsByVersionNumBacks(versionNumBacks);
                    itemService.deleteByIds(itemIds);
                    itemAppService.deleteByItemIds(itemIds);
                    itemResourceService.deleteByItemIds(itemIds);
                }
            }
            //获取版本号，然后删除改版本号的所有
            String[] versionNumBacks = itemService.getVersionNumBacksByIds(ids);
            long[] useItemIds = itemService.getUseIdsByVersionNumBacks(versionNumBacks);
            itemService.deleteAndUpdateTimeByIds(useItemIds);
            long[] itemIds = itemService.getIdsByVersionNumBacks(versionNumBacks);
            itemService.deleteByIds(itemIds);
            itemAppService.deleteByItemIds(itemIds);
            itemResourceService.deleteByItemIds(itemIds);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除公共配置条目出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除公共配置条目出错");
        }
    }

    /**
     * @param request        http请求
     * @param file           文件
     * @param instanceTypeId 配置文件类型id
     * @param versionId      版本id
     * @param itemId         条目id
     * @param envId          环境id
     * @return java.lang.Object 上传结果
     * @description 上传实例数据
     * @author maodi
     * @createDate 2018/7/5 17:15
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    Object upload(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam
            ("instanceTypeId") long instanceTypeId, @RequestParam("versionId") int versionId, @RequestParam(value =
            "itemId", required = false, defaultValue = "-1") long itemId, @RequestParam(value = "envId", required =
            false, defaultValue = "-1") long envId) {
        try {
            if (file.isEmpty()) {
                return "文件为空";
            }
            String fileEncode = StringUtil.getFileEncode(file.getInputStream());
            if (fileEncode.indexOf("BOM") != -1) {
                return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "上传失败！该文件为UTF-8 " +
                                                                                       "BOM格式！");
            }
            String encoding = StringUtil.getEncode(file.getInputStream());
            String fileStr = IOUtils.toString(file.getInputStream(), encoding);
            OrderedProperties prop = new OrderedProperties();
            boolean isValid = false;
            String fileType = "";
            if (instanceTypeId == 0) {
                prop.load(new InputStreamReader(file.getInputStream(), encoding));
                isValid = true;
                fileType = "properties";
            } else if (instanceTypeId == 1) {
                isValid = StringUtil.validJSONFile(fileStr);
                fileType = "json";
            } else if (instanceTypeId == 2) {
                isValid = StringUtil.validXmlFile(fileStr);
                fileType = "xml";
            } else if (instanceTypeId == 3) {
                isValid = StringUtil.validYmlFile(fileStr);
                fileType = "yml";
            }
            if (!isValid) {
                return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, fileType + "文件格式不对");
            }
            Item item = new Item();
            String username = request.getSession().getAttribute(Constant.SESSION_KEY).toString();
            item.setReleaseStatus(0);
            item.setVersionId(versionId);
            item.setLineNum(0);
            item.setMustChange(0);
            Timestamp timestamp = StringUtil.getNowTimestamp();
            item.setLastUpdateTime(timestamp);
            item.setUpdateTime(timestamp);
            long userId = userService.getIdByName(username);
            item.setLastUpdateBy(userId);
            item.setIsPublic(1);
            item.setInstanceTypeId((int) instanceTypeId);
            item.setEnvId(envId);
            List<String> mainKeyList = new ArrayList<>();
            if (versionId == 1) {
                String[] keys = itemService.getPublicMainKeysByInstanceTypeId(instanceTypeId);
                if (keys.length > 0) {
                    mainKeyList = Arrays.asList(keys);
                } else {
                    return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                }
            }
            if (instanceTypeId != 0) {
                item.setId(StringUtil.getId());
                item.setKey("content");
                item.setValue(fileStr);
                item.setVersionNum(StringUtil.getVersionNum(0, versionId, 1, 0));
                if (versionId == 0) {
                    if (itemId != -1) {
                        long[] itemIds = {itemId};
                        itemService.deleteByIds(itemIds);
                    }
                    itemService.save(item);
                    return new ConfigResponse(HttpStatus.OK.value(), 1, "上传成功！");
                } else {
                    if (mainKeyList.contains("content")) {
                        if (itemId != -1) {
                            long[] itemIds = {itemId};
                            itemService.deleteByIds(itemIds);
                        }
                        itemService.save(item);
                        return new ConfigResponse(HttpStatus.OK.value(), 1, "上传成功！");
                    } else {
                        return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                    }
                }
            } else {
                int num = 0;
                int uploadCount = 0;
                int noMainCount = 0;
                int keyNotPubCount = 0;
                int lineNum = 0;
                Set<String> propertyNames = prop.stringPropertyNames();
                for (String name : propertyNames) {
                    if (!name.startsWith("pub.")) {
                        keyNotPubCount++;
                    } else {
                        name = StringUtil.getNoOtherLanguage(name);
                        name = StringUtil.getNoStartNumStr(name);
                        Map<String, Object> itemCountMap = new HashMap<>(16);
                        itemCountMap.put("key", name);
                        itemCountMap.put("instanceTypeId", instanceTypeId);
                        itemCountMap.put("versionId", versionId);
                        itemCountMap.put("envId", envId);
                        lineNum++;
                        int itemCount = itemService.countByMap(itemCountMap);
                        String value = prop.getProperty(name);
                        if (itemCount < 1) {
                            item.setId(StringUtil.getId() + num++);
                            item.setKey(name);
                            item.setValue(value);
                            item.setVersionNum(StringUtil.getVersionNum(0, versionId, 1, num));
                            item.setLineNum(lineNum);
                            if (versionId == 0) {
                                itemService.save(item);
                                uploadCount++;
                            } else {
                                if (mainKeyList.contains(name)) {
                                    itemService.save(item);
                                    uploadCount++;
                                } else {
                                    noMainCount++;
                                }
                            }
                        } else {
                            Item[] duplicateItems = itemService.getDuplicateItemByMap(itemCountMap);
                            for (Item duplicateItem : duplicateItems) {
                                duplicateItem.setValue(value);
                                duplicateItem.setLastUpdateTime(timestamp);
                                duplicateItem.setUpdateTime(timestamp);
                                duplicateItem.setLastUpdateBy(userId);
                                itemService.update(duplicateItem);
                                uploadCount++;
                            }
                        }
                    }
                }
                int size = propertyNames.size();
                String message = uploadCount + "条上传成功";
                if (versionId == 1) {
                    if (noMainCount > 0) {
                        message += "，" + noMainCount + "条没有对应主版本";
                    }
                }
                if (keyNotPubCount > 0) {
                    message += "，" + keyNotPubCount + "条键没有以pub.开头";
                }
                if (uploadCount < size) {
                    return new ConfigResponse(HttpStatus.OK.value(), uploadCount, message);
                } else {
                    return new ConfigResponse(HttpStatus.OK.value(), size, "上传成功！");
                }
            }
        } catch (Exception e) {
            log.error("公共配置上传失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "上传失败！请联系管理员！");
        }
    }

    /**
     * @param request  http请求
     * @param response http响应
     * @param username 用户名
     * @return void
     * @description 下载实例
     * @author maodi
     * @createDate 2018/7/5 17:16
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public @ResponseBody
    void download(HttpServletRequest request, HttpServletResponse response, @SessionAttribute(Constant.SESSION_KEY)
            String username) {
        // 设置文件名，根据业务需要替换成要下载的文件名
        long instanceTypeId = Long.valueOf(request.getParameter("instanceTypeId"));
        String[] itemIds = request.getParameterValues("itemIds[]");
        InstanceType instanceType = instanceTypeService.get(instanceTypeId);
        String timeFormat = DateUtil.timestamp2Date(System.currentTimeMillis(), "yyyy-MM-dd-HH");
        String fileName = "public-(" + timeFormat + ")." + instanceType.getName();
        if (fileName != null) {
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            byte[] buffer = new byte[1024];
            InputStream fis = null;
            BufferedInputStream bis = null;
            List<Map<String, Object>> listMap;
            String isAllDownload = request.getParameter("isAllDownload");
            if (StringUtils.isNotBlank(isAllDownload)) {
                listMap = publicConfigService.getAllKeyAndValue(request, username);
            } else {
                long[] ids = new long[itemIds.length];
                int tempCount = 0;
                for (String itemIdStr : itemIds) {
                    ids[tempCount++] = Long.valueOf(itemIdStr);
                }
                listMap = itemService.getPropertiesByIds(ids);
            }
            try {
                request.setCharacterEncoding("UTF-8");
                StringBuilder lineSb = new StringBuilder();
                OutputStream os = response.getOutputStream();
                for (Map<String, Object> itemMap : listMap) {
                    String line = itemMap.get("value") + "\r\n";
                    if (instanceTypeId == 0) {
                        line = itemMap.get("key") + "=" + line;
                    }
                    lineSb.append(line);
                }
                lineSb = new StringBuilder(lineSb.toString().substring(0, lineSb.toString().length() - 1));
                fis = new ByteArrayInputStream(lineSb.toString().getBytes());
                bis = new BufferedInputStream(fis);
                int i;
                while ((i = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, i);
                }
                os.close();
            } catch (Exception e) {
                log.error("下载文件出错", e);
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        log.error("关闭buffer流出错", e);
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        log.error("关闭文件流出错", e);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @param ids       条目id数组
     * @param versionId 主灰版本id
     * @param username  用户名
     * @return java.lang.Object 发布结果
     * @description 发布条目
     * @author maodi
     * @createDate 2018/7/5 17:16
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/release")
    public @ResponseBody
    Object release(long[] ids, long versionId, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            itemService.releaseByIds(StringUtil.actionMapFrontVersion(ids, userService.getIdByName(username), StringUtil
                    .getFrontVersion(1, versionId, 1)));
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "发布成功！");
        } catch (Exception e) {
            log.error("根据ids发布公共配置实例配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids发布公共配置实例配置出错");
        }
    }

    /**
     * @param ids       条目id数组
     * @param versionId 主灰版本id
     * @param username  用户名
     * @return java.lang.Object 下线结果
     * @description 下线条目
     * @author maodi
     * @createDate 2018/7/5 17:17
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/offline")
    public @ResponseBody
    Object offline(long[] ids, long versionId, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            //新建历史发布数据
            List<Item> list = itemService.getByIds(ids);
            int count = 0;
            for (Item item : list) {
                long oldItemId = item.getId();
                long newItemId = StringUtil.getId() + count++;
                item.setId(newItemId);
                item.setIsDelete(2);
                item.setIsPublic(1);
                itemService.save(item);
                itemResourceSave(itemResourceService.getResourceIdsByItemId(oldItemId), newItemId, item
                        .getInstanceTypeId());
                long[] appIds = itemAppService.getAppIdsByItemId(oldItemId);
                if (appIds != null && appIds.length > 0) {
                    itemAppSave(appIds, newItemId);
                }
                long[] itemIds = {newItemId};
                itemAppService.overByItemIds(itemIds);
                itemResourceService.overByItemIds(itemIds);
            }
            itemService.offlineByIds(StringUtil.actionMapFrontVersion(ids, userService.getIdByName(username), StringUtil
                    .getFrontVersion(0, versionId, 1)));
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "下线成功！");
        } catch (Exception e) {
            log.error("根据ids下线公共配置实例配置出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids下线公共配置实例配置出错");
        }
    }

    /**
     * @param resourceIds 资源的id数组
     * @return java.lang.String 处理过后的信息
     * @description 处理重复的信息
     * @author maodi
     * @createDate 2018/8/31 14:31
     */
    private String getDuplicateInfo(long[] resourceIds) {
        List<Map<String, Object>> list = publicConfigService.getDuplicateProductAndModuleAndEnvByResourceIds
                (resourceIds);
        StringBuilder sb = new StringBuilder("您输入的内容不能与");
        for (Map<String, Object> map : list) {
            String productName = map.get("productName").toString().endsWith("项目") ? map.get("productName").toString()
                    : map.get("productName").toString() + "项目";
            String moduleName = map.get("moduleName").toString().endsWith("模块") ? map.get("moduleName").toString() :
                    map.get("moduleName").toString() + "模块";
            String envName = map.get("envName").toString().endsWith("环境") ? map.get("envName").toString() : map.get
                    ("envName").toString() + "环境";
            sb.append(productName + moduleName + envName + "、");
        }
        if (sb.length() > 9) {
            sb = new StringBuilder(sb.substring(0, sb.length() - 1));
        } else {
            sb.append("当前公共");
        }
        sb.append("配置重复");
        return sb.toString();
    }

    /**
     * @param listMap 数据的listmap
     * @return long[] 资源id数组
     * @description 获取被选中的资源ids通过listmap
     * @author maodi
     * @createDate 2018/8/31 14:32
     */
    private long[] getSelectResourceIdsByListMap(List<Map<String, Object>> listMap) {
        Set<Long> set = new HashSet<>();
        for (int i = 0, size = listMap.size(); i < size; i++) {
            Map<String, Object> map = listMap.get(i);
            long resourceId = Long.valueOf(map.get("id").toString());
            int isSelect = Integer.valueOf(map.get("is_select").toString());
            if (isSelect == 1) {
                set.add(resourceId);
            }
        }
        return StringUtil.longSetAsArray(set);
    }

    /**
     * @param listMap 数据的listmap
     * @return java.util.Set<java.lang.Long>
     * @description 获取所有的资源ids通过listmap
     * @author maodi
     * @createDate 2018/8/31 14:33
     */
    private Set<Long> getAllResourceIdsSetByListMap(List<Map<String, Object>> listMap) {
        Set<Long> set = new HashSet<>();
        for (int i = 0, size = listMap.size(); i < size; i++) {
            Map<String, Object> map = listMap.get(i);
            long resourceId = Long.valueOf(map.get("id").toString());
            set.add(resourceId);
        }
        return set;
    }

    /**
     * @param listMap              数据listmap
     * @param newAllResourceIdsSet 新资源id的set
     * @return long[]
     * @description 获取老的资源ids
     * @author maodi
     * @createDate 2018/8/31 14:33
     */
    private long[] getOldResourceIds(List<Map<String, Object>> listMap, Set<Long> newAllResourceIdsSet) {
        Set<Long> oldResourceIdSet = new HashSet<>();
        for (Map<String, Object> tempMap : listMap) {
            long oldResourceId = Long.valueOf(tempMap.get("id").toString());
            int isSelect = Integer.valueOf(tempMap.get("is_select").toString());
            if (isSelect == 1 && !newAllResourceIdsSet.contains(oldResourceId)) {
                oldResourceIdSet.add(oldResourceId);
            }
        }
        return StringUtil.longSetAsArray(oldResourceIdSet);
    }

    /**
     * @param key                    键
     * @param itemId                 条目id
     * @param publicApplyResourceIds 公共中配置的资源的id数组
     * @return long[]  重复的资源id数组
     * @description 获取重复的资源id数组
     * @author maodi
     * @createDate 2018/9/7 16:03
     */
    private long[] getProductResourceIds(String key, long itemId, long[] publicApplyResourceIds) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("key", key);
        map.put("id", itemId);
        map.put("resourceIds", publicApplyResourceIds);
        return itemService.getDuplicateProductResourceIdsByMap(map);
    }

}