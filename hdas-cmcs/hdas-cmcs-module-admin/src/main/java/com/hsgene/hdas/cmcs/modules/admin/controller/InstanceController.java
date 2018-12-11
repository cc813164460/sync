package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.hsgene.hdas.cmcs.modules.admin.domain.*;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.*;
import com.hsgene.hdas.cmcs.modules.admin.util.OrderedProperties;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @description: 项目配置控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/instance")
public class InstanceController {

    @Autowired
    private IResourceService resourceService;

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
    private IProductService productService;

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IEnvService envService;

    @Autowired
    private IRoleClassService roleClassService;

    /**
     * @param productModuleEnvId 项目-模块-环境id
     * @param model              返回给前台页面能够接收到的对象
     * @return java.lang.String   配置页面名字资源等参数
     * @description 获取配置页面并返回资源等参数
     * @author maodi
     * @createDate 2018/9/7 15:50
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public String page(String productModuleEnvId, Model model) {
        String[] ids = productModuleEnvId.split("_");
        long productId = Long.valueOf(ids[0]);
        long moduleId = Long.valueOf(ids[1]);
        long envId = Long.valueOf(ids[2]);
        Map<String, Object> map = new HashMap<>(16);
        map.put("productId", productId);
        map.put("moduleId", moduleId);
        map.put("envId", envId);
        map.put("versionId", 0);
        long mainResourceId = resourceService.getIdByProductModuleEnvVersionId(map);
        map.put("versionId", 1);
        long grayResourceId = resourceService.getIdByProductModuleEnvVersionId(map);
        model.addAttribute("productModuleEnvId", productModuleEnvId);
        model.addAttribute("mainResourceId", mainResourceId);
        model.addAttribute("grayResourceId", grayResourceId);
        return "views/common/instance";
    }

    /**
     * @param
     * @return java.lang.String  主版本新增页名字
     * @description 获取主版本配置新增页
     * @author maodi
     * @createDate 2018/6/13 15:38
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "views/common/instance_add";
    }

    /**
     * @param
     * @return java.lang.String 主版本配置修改页名字
     * @description 获取主版本配置修改页
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "views/common/instance_update";
    }

    /**
     * @param
     * @return java.lang.String  灰度版本新增页名字
     * @description 获取灰度版本配置新增页
     * @author maodi
     * @createDate 2018/6/13 15:38
     */
    @RequestMapping(value = "/gray_add_page", method = RequestMethod.GET)
    public String grayAddPage() {
        return "views/common/instance_gray_add";
    }

    /**
     * @param
     * @return java.lang.String 灰度版本配置修改页名字
     * @description 获取灰度版本配置修改页
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping(value = "/gray_update_page", method = RequestMethod.GET)
    public String grayUpdatePage() {
        return "views/common/instance_gray_update";
    }


    /**
     * @param
     * @return java.lang.String  主版本复制配置页面名字
     * @description 获取主版本复制配置页面
     * @author maodi
     * @createDate 2018/8/31 14:19
     */
    @RequestMapping(value = "/copy_config_page", method = RequestMethod.GET)
    public String copyConfig() {
        return "views/common/instance_copy_config";
    }

    /**
     * @param
     * @return java.lang.String 灰度版本复制配置页面名字
     * @description 获取灰度版本复制配置页面
     * @author maodi
     * @createDate 2018/8/31 14:20
     */
    @RequestMapping(value = "/gray_copy_config_page", method = RequestMethod.GET)
    public String grayCopyConfig() {
        return "views/common/instance_gray_copy_config";
    }

    /**
     * @param hsr      http请求
     * @param username 用户名
     * @return java.lang.Object 项目properties分页数据
     * @description 获取项目properties分页数据
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/query_properties")
    public @ResponseBody
    Object getQueryProperties(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            if ("admin".equals(username)) {
                maxClassId = 2;
            }
            String productModuleEnvId = hsr.getParameter("productModuleEnvId");
            long versionId = Long.valueOf(hsr.getParameter("versionId"));
            String[] ids = productModuleEnvId.split("_");
            long productId = Long.valueOf(ids[0]);
            long moduleId = Long.valueOf(ids[1]);
            long envId = Long.valueOf(ids[2]);
            Map<String, Object> map = new HashMap<>(16);
            map.put("productId", productId);
            map.put("moduleId", moduleId);
            map.put("envId", envId);
            map.put("versionId", versionId);
            long resourceId = resourceService.getIdByProductModuleEnvVersionId(map);
            map.put("versionId", 0);
            long mainResourceId = resourceService.getIdByProductModuleEnvVersionId(map);
            map = new HashMap<>(16);
            map.put("resourceId", mainResourceId);
            map.put("instanceTypeId", 0);
            long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            return instanceService.selectPropertiesByPage(hsr, resourceId, instanceId, maxClassId);
        } catch (Exception e) {
            log.error("获取项目properties分页数据出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取项目properties分页数据出错");
        }
    }

    /**
     * @param hsr http请求
     * @return java.lang.Object 除properties外配置的数据
     * @description 获取除properties外配置的数据
     * @author maodi
     * @createDate 2018/7/5 17:11
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/query_exclude_properties")
    public @ResponseBody
    Object getQueryExcludeProperties(HttpServletRequest hsr) {
        try {
            String productModuleEnvId = hsr.getParameter("productModuleEnvId");
            long versionId = Long.valueOf(hsr.getParameter("versionId"));
            long instanceTypeId = Long.valueOf(hsr.getParameter("instanceTypeId"));
            String[] ids = productModuleEnvId.split("_");
            long productId = Long.valueOf(ids[0]);
            long moduleId = Long.valueOf(ids[1]);
            long envId = Long.valueOf(ids[2]);
            Map<String, Object> map = new HashMap<>(16);
            map.put("productId", productId);
            map.put("moduleId", moduleId);
            map.put("envId", envId);
            map.put("versionId", versionId);
            long resourceId = resourceService.getIdByProductModuleEnvVersionId(map);
            map = new HashMap<>(16);
            map.put("resourceId", resourceId);
            map.put("instanceTypeId", instanceTypeId);
            long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            if (instanceId == -1) {
                return null;
            } else {
                List<Map<String, Object>> list = itemService.getExcludePropertiesByInstanceId(instanceId);
                if (list.size() < 1) {
                    list = itemService.getExcludePropertiesByResourceId(resourceId);
                }
                //美化json数据
                if (instanceTypeId == 1) {
                    for (Map<String, Object> tempMap : list) {
                        Object obj = tempMap.get("value");
                        if (obj != null) {
                            tempMap.put("value", StringUtil.formatJSONFile(obj.toString()));
                        }
                    }
                }
                return list;
            }
        } catch (Exception e) {
            log.error("获取项目其他配置数据出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取项目其他配置数据出错");
        }
    }

    /**
     * @param json     配置信息
     * @param username 用户名
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse  复制结果
     * @description 复制配置信息
     * @author maodi
     * @createDate 2018/8/31 14:22
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
            String productModuleEnvId = copyConfigData.getProductModuleEnvId();
            long resourceId = copyConfigData.getResourceId();
            String[] ids = productModuleEnvId.split("_");
            int versionId = copyConfigData.getVersionId();
            List<Map<String, Object>> copyConfigList = copyConfigData.getCopyConfigList();
            long productId = Long.valueOf(ids[0]);
            long moduleId = Long.valueOf(ids[1]);
            long envId = Long.valueOf(ids[2]);
            List<String> mainKeyList = new ArrayList<>();
            if (versionId == 1) {
                Map<String, Object> resourceMap = new HashMap<>(16);
                resourceMap.put("productId", productId);
                resourceMap.put("moduleId", moduleId);
                resourceMap.put("envId", envId);
                resourceMap.put("versionId", 0);
                long mainResourceId = resourceService.getIdByProductModuleEnvVersionId(resourceMap);
                Map<String, Object> instanceMap = new HashMap<>(16);
                instanceMap.put("instanceTypeId", instanceTypeId);
                instanceMap.put("resourceId", mainResourceId);
                long mainInstanceId = instanceService.getIdByResourceIdAndInstanceTypeId(instanceMap);
                if (mainInstanceId != -1) {
                    mainKeyList = Arrays.asList(itemService.getKeysByInstanceId(mainInstanceId));
                }
            }
            Map<String, Object> map = new HashMap<>(16);
            map.put("instanceTypeId", instanceTypeId);
            map.put("resourceId", resourceId);
            int num = instanceService.countByMap(map);
            long instanceId = StringUtil.getId();
            if (num < 1) {
                Instance instance = new Instance();
                instance.setId(StringUtil.getId());
                instance.setResourceId(String.valueOf(resourceId));
                instance.setInstanceTypeId(instanceTypeId);
                InstanceType instanceType = instanceTypeService.get(instanceTypeId);
                instance.setName(instanceType.getName());
                instanceService.save(instance);
            } else {
                instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            }
            long userId = userService.getIdByName(username);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            int lineNum = 0;
            StringBuilder sbNoMain = new StringBuilder();
            StringBuilder sbProduct = new StringBuilder();
            StringBuilder sbPublic = new StringBuilder();
            int successCount = 0;
            for (Map<String, Object> tempMap : copyConfigList) {
                String key = tempMap.get("key").toString();
                Item item = new Item();
                item.setId(StringUtil.getId());
                item.setVersionNum(tempMap.get("versionNum").toString());
                item.setKey(key);
                item.setValue(tempMap.get("value").toString());
                item.setInstanceId(instanceId);
                item.setInstanceTypeId(instanceTypeId);
                item.setVersionId(versionId);
                item.setReleaseStatus(0);
                item.setLineNum(lineNum++);
                item.setClassId(Integer.valueOf(tempMap.get("classId").toString()));
                item.setComment(tempMap.get("comment").toString());
                item.setLastUpdateTime(timestamp);
                item.setUpdateTime(timestamp);
                item.setLastUpdateBy(userId);
                item.setMustChange(Integer.valueOf(tempMap.get("mustChange").toString()));
                Map<String, Object> itemCountMap = new HashMap<>(16);
                itemCountMap.put("instanceId", instanceId);
                itemCountMap.put("resourceId", resourceId);
                itemCountMap.put("key", key);
                int itemCount = itemService.countByMap(itemCountMap);
                if (itemCount < 1) {
                    if (versionId == 0) {
                        itemService.save(item);
                    } else {
                        if (mainKeyList.contains(key)) {
                            itemService.save(item);
                            successCount++;
                        } else {
                            sbNoMain.append(key + ",");
                        }
                    }
                } else {
                    if ("0".equals(getCopyDuplicateInfo(itemCountMap))) {
                        sbPublic.append(key + ",");
                    } else {
                        //再次复制就是修改
                        Item currentItem = itemService.getDuplicateProductItemByMap(itemCountMap);
                        item.setId(currentItem.getId());
                        item.setVersionNum(currentItem.getVersionNum());
                        itemService.update(item);
                        successCount++;
                    }
                }
            }
            return new ConfigResponse(HttpStatus.OK.value(), 1, dealMessage(sbNoMain, sbPublic, sbProduct,
                    successCount));
        } catch (Exception e) {
            log.error("复制项目配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "复制项目配置出错");
        }
    }

    /**
     * @param instanceInfo 项目数据
     * @param username     用户名
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse
     * @description 新增项目配置信息
     * @author maodi
     * @createDate 2018/6/28 17:26
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(InstanceInfo instanceInfo, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long instanceTypeId = instanceInfo.getInstanceTypeId();
            long resourceId = instanceInfo.getResourceId();
            String comment = instanceInfo.getComment();
            String key = instanceInfo.getKey();
            String value = instanceInfo.getValue();
            int versionId = instanceInfo.getVersionId();
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
            Map<String, Object> map = new HashMap<>(16);
            map.put("instanceTypeId", instanceTypeId);
            map.put("resourceId", resourceId);
            int num = instanceService.countByMap(map);
            long instanceId = StringUtil.getId();
            List<String> mainKeyList = new ArrayList<>();
            Resource resource = resourceService.get(resourceId);
            long productId = resource.getProductId();
            long moduleId = resource.getModuleId();
            long envId = resource.getEnvId();
            if (versionId == 1) {
                Map<String, Object> resourceMap = new HashMap<>(16);
                resourceMap.put("productId", productId);
                resourceMap.put("moduleId", moduleId);
                resourceMap.put("envId", envId);
                resourceMap.put("versionId", 0);
                long mainResourceId = resourceService.getIdByProductModuleEnvVersionId(resourceMap);
                Map<String, Object> instanceMap = new HashMap<>(16);
                instanceMap.put("instanceTypeId", instanceTypeId);
                instanceMap.put("resourceId", mainResourceId);
                long mainInstanceId = instanceService.getIdByResourceIdAndInstanceTypeId(instanceMap);
                if (mainInstanceId != -1) {
                    mainKeyList = Arrays.asList(itemService.getKeysByInstanceId(mainInstanceId));
                } else {
                    return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                }
            }
            if (num < 1) {
                Instance instance = new Instance();
                instance.setId(instanceId);
                instance.setResourceId(String.valueOf(instanceInfo.getResourceId()));
                instance.setInstanceTypeId((int) instanceTypeId);
                InstanceType instanceType = instanceTypeService.get(instanceTypeId);
                instance.setName(instanceType.getName());
                instanceService.save(instance);
            } else {
                instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            }
            Item item = new Item();
            long itemId = StringUtil.getId();
            if (instanceInfo.getItemId() != -1) {
                itemId = instanceInfo.getItemId();
            }
            item.setId(itemId);
            item.setComment(comment);
            item.setKey(key);
            item.setValue(value);
            item.setInstanceId(instanceId);
            item.setLastUpdateBy(userService.getIdByName(username));
            Timestamp updateTime = new Timestamp(System.currentTimeMillis());
            item.setLastUpdateTime(updateTime);
            item.setUpdateTime(updateTime);
            item.setInstanceTypeId((int) instanceTypeId);
            item.setVersionId(versionId);
            item.setReleaseStatus(0);
            item.setLineNum(instanceInfo.getLineNum());
            item.setVersionNum(instanceInfo.getVersionNum() == null ? StringUtil.getVersionNum(0, versionId, 0, 0) :
                    instanceInfo.getVersionNum());
            item.setInstanceTypeId((int) instanceTypeId);
            item.setClassId(instanceInfo.getClassId());
            item.setMustChange(instanceInfo.getMustChange());
            Map<String, Object> itemCountMap = new HashMap<>(16);
            itemCountMap.put("instanceId", instanceId);
            itemCountMap.put("resourceId", resourceId);
            itemCountMap.put("key", key);
            int itemCount = itemService.countByMap(itemCountMap);
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
                return getDuplicateInfo(itemCountMap);
            }
            if (appIds != null && appIds.length > 0) {
                itemAppSave(appIds, itemId);
            }
            return new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
        } catch (Exception e) {
            log.error("新增项目配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增项目配置出错");
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
     * @param hsr      http请求，包含参数
     * @param username 用户名
     * @return java.lang.Object 项目properties分页数据
     * @description 根据key获取项目properties分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByKeyPage(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            if ("admin".equals(username)) {
                maxClassId = 2;
            }
            String productModuleEnvId = hsr.getParameter("productModuleEnvId");
            String key = hsr.getParameter("name");
            long versionId = Long.valueOf(hsr.getParameter("versionId"));
            String[] ids = productModuleEnvId.split("_");
            long productId = Long.valueOf(ids[0]);
            long moduleId = Long.valueOf(ids[1]);
            long envId = Long.valueOf(ids[2]);
            Map<String, Object> map = new HashMap<>(16);
            map.put("productId", productId);
            map.put("moduleId", moduleId);
            map.put("envId", envId);
            map.put("versionId", versionId);
            long resourceId = resourceService.getIdByProductModuleEnvVersionId(map);
            map.put("versionId", 0);
            long mainResourceId = resourceService.getIdByProductModuleEnvVersionId(map);
            map = new HashMap<>(16);
            map.put("resourceId", mainResourceId);
            map.put("instanceTypeId", 0);
            long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            return instanceService.selectPropertiesByKeyPage(hsr, resourceId, key, instanceId, maxClassId);
        } catch (Exception e) {
            log.error("根据key获取项目properties分页数据出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据key获取项目properties分页数据出错");
        }
    }

    /**
     * @param instanceInfo 项目数据
     * @param username     用户名
     * @return java.lang.Object
     * @description 更新项目数据
     * @author maodi
     * @createDate 2018/6/28 17:27
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(InstanceInfo instanceInfo, @SessionAttribute(Constant.SESSION_KEY) String username) {
        ConfigResponse configResponse;
        try {

            long instanceTypeId = instanceInfo.getInstanceTypeId();
            long resourceId = instanceInfo.getResourceId();
            long[] appIds = instanceInfo.getAppIds();
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
            int versionId = instanceInfo.getVersionId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("instanceTypeId", instanceTypeId);
            map.put("resourceId", resourceId);
            long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
            Item item = new Item();
            long itemId = instanceInfo.getItemId();
            Map<String, Object> itemCountMap = new HashMap<>(16);
            itemCountMap.put("instanceId", instanceId);
            itemCountMap.put("key", key);
            itemCountMap.put("id", itemId);
            itemCountMap.put("resourceId", resourceId);
            int itemCount = itemService.countByMap(itemCountMap);
            if (instanceTypeId != 0) {
                long[] resourceIds = {resourceId};
                Map<String, Object> tempMap = new HashMap<>(16);
                tempMap.put("key", key);
                tempMap.put("resourceIds", resourceIds);
                itemCount = itemService.countPublic(tempMap);
            }
            if (itemCount > 0) {
                return getDuplicateInfo(itemCountMap);
            }
            long newItemId = StringUtil.getId();
            item.setId(newItemId);
            item.setComment(comment);
            item.setKey(key);
            item.setValue(value);
            item.setInstanceId(instanceId);
            item.setLastUpdateBy(userService.getIdByName(username));
            Timestamp updateTime = StringUtil.getNowTimestamp();
            item.setLastUpdateTime(updateTime);
            item.setUpdateTime(updateTime);
            item.setVersionId(versionId);
            item.setReleaseStatus(releaseStatus);
            item.setLineNum(instanceInfo.getLineNum());
            item.setVersionNum(instanceInfo.getVersionNum());
            item.setInstanceTypeId((int) instanceTypeId);
            item.setClassId(instanceInfo.getClassId());
            item.setMustChange(instanceInfo.getMustChange());
            long[] itemIds = {itemId};
            itemService.overByIds(itemIds);
            itemAppService.overByItemIds(itemIds);
            itemService.save(item);
            if (appIds != null && appIds.length > 0) {
                itemAppSave(appIds, newItemId);
            }
            configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
        } catch (Exception e) {
            log.error("修改项目配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改项目配置出错");
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
            long[] instanceIds = new long[list.size()];
            List<String> keyList = new ArrayList<>();
            int versionId = 1;
            int instanceTypeId = 0;
            for (int i = 0, length = list.size(); i < length; i++) {
                Item item = list.get(i);
                versionId = item.getVersionId();
                instanceTypeId = item.getInstanceTypeId();
                instanceIds[i] = item.getInstanceId();
                keyList.add(item.getKey());
            }
            //如果为主版本ids，获取灰度版本对应的ids，删除灰度版本
            if (versionId == 0) {
                long mainResourceId = instanceService.getResourceIdByIds(instanceIds);
                Resource resource = resourceService.get(mainResourceId);
                Map<String, Object> map = new HashMap<>(16);
                map.put("productId", resource.getProductId());
                map.put("moduleId", resource.getModuleId());
                map.put("envId", resource.getEnvId());
                map.put("versionId", 1);
                long grayResourceId = resourceService.getIdByProductModuleEnvVersionId(map);
                map = new HashMap<>(16);
                map.put("resourceId", grayResourceId);
                map.put("instanceTypeId", instanceTypeId);
                long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(map);
                map = new HashMap<>(16);
                map.put("instanceId", instanceId);
                map.put("keys", keyList);
                long[] grayIds = itemService.getIdsByInstanceIdAndKeys(map);
                if (grayIds.length > 0) {
                    //获取版本号，然后删除改版本号的所有
                    String[] versionNumBacks = itemService.getVersionNumBacksByIds(grayIds);
                    long[] useItemIds = itemService.getUseIdsByVersionNumBacks(versionNumBacks);
                    itemService.deleteAndUpdateTimeByIds(useItemIds);
                    long[] itemIds = itemService.getIdsByVersionNumBacks(versionNumBacks);
                    itemService.deleteByIds(itemIds);
                    itemAppService.deleteByItemIds(itemIds);
                }
            }
            //获取版本号，然后删除改版本号的所有
            String[] versionNumBacks = itemService.getVersionNumBacksByIds(ids);
            long[] useItemIds = itemService.getUseIdsByVersionNumBacks(versionNumBacks);
            itemService.deleteAndUpdateTimeByIds(useItemIds);
            long[] itemIds = itemService.getIdsByVersionNumBacks(versionNumBacks);
            itemService.deleteByIds(itemIds);
            itemAppService.deleteByItemIds(itemIds);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除条目出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除条目出错");
        }
    }

    /**
     * @param request            http请求
     * @param file               文件
     * @param productModuleEnvId 项目-模块-环境id
     * @param instanceTypeId     配置文件类型id
     * @param versionId          版本id
     * @param itemId             条目id
     * @return java.lang.Object 上传结果
     * @description 上传项目配置数据
     * @author maodi
     * @createDate 2018/7/5 17:15
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    Object upload(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam
            ("productModuleEnvId") String productModuleEnvId, @RequestParam("instanceTypeId") long instanceTypeId,
                  @RequestParam("versionId") int versionId, @RequestParam(value = "itemId", required = false,
            defaultValue = "-1") long itemId) {
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
                return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, fileType + "文件内容格式不对");
            }
            Item item = new Item();
            String[] ids = productModuleEnvId.split("_");
            long productId = Long.valueOf(ids[0]);
            long moduleId = Long.valueOf(ids[1]);
            long envId = Long.valueOf(ids[2]);
            String username = request.getSession().getAttribute(Constant.SESSION_KEY).toString();
            item.setReleaseStatus(0);
            item.setVersionId(versionId);
            item.setLineNum(0);
            item.setMustChange(0);
            item.setInstanceTypeId((int) instanceTypeId);
            Timestamp timestamp = StringUtil.getNowTimestamp();
            item.setLastUpdateTime(timestamp);
            item.setUpdateTime(timestamp);
            long userId = userService.getIdByName(username);
            item.setLastUpdateBy(userId);
            Map<String, Object> map = new HashMap<>(16);
            map.put("productId", productId);
            map.put("moduleId", moduleId);
            map.put("envId", envId);
            map.put("versionId", versionId);
            long resourceId = resourceService.getIdByProductModuleEnvVersionId(map);
            long mainResourceId = resourceId;
            if (versionId == 1) {
                Map<String, Object> resourceMap = new HashMap<>(16);
                resourceMap.put("productId", productId);
                resourceMap.put("moduleId", moduleId);
                resourceMap.put("envId", envId);
                resourceMap.put("versionId", 0);
                mainResourceId = resourceService.getIdByProductModuleEnvVersionId(resourceMap);
            }
            Map<String, Object> instanceMap = new HashMap<>(16);
            instanceMap.put("instanceTypeId", instanceTypeId);
            instanceMap.put("resourceId", resourceId);
            long instanceId = instanceService.getIdByResourceIdAndInstanceTypeId(instanceMap);
            Map<String, Object> mainInstanceMap = new HashMap<>(16);
            mainInstanceMap.put("instanceTypeId", instanceTypeId);
            mainInstanceMap.put("resourceId", mainResourceId);
            long mainInstanceId = instanceService.getIdByResourceIdAndInstanceTypeId(mainInstanceMap);
            if (instanceId == -1) {
                instanceId = StringUtil.getId();
                Instance instance = new Instance();
                instance.setId(instanceId);
                instance.setResourceId(String.valueOf(resourceId));
                instance.setInstanceTypeId((int) instanceTypeId);
                InstanceType instanceType = instanceTypeService.get(instanceTypeId);
                instance.setName(instanceType.getName());
                instanceService.save(instance);
            }
            item.setInstanceId(instanceId);
            List<String> mainKeyList = new ArrayList<>();
            if (versionId == 1) {
                if (mainInstanceId != -1) {
                    mainKeyList = Arrays.asList(itemService.getKeysByInstanceId(mainInstanceId));
                } else {
                    return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本配置！");
                }
            }
            if (instanceTypeId != 0) {
                item.setId(StringUtil.getId());
                item.setKey("content");
                item.setValue(fileStr);
                if (versionId == 0) {
                    if (itemId != -1) {
                        long[] itemIds = {itemId};
                        itemService.deleteByIds(itemIds);
                    }
                    item.setVersionNum(StringUtil.getVersionNum(0, versionId, 0, 0));
                    itemService.save(item);
                    return new ConfigResponse(HttpStatus.OK.value(), 1, "上传成功！");
                } else {
                    if (mainKeyList.contains("content")) {
                        if (itemId != -1) {
                            long[] itemIds = {itemId};
                            itemService.deleteByIds(itemIds);
                        }
                        item.setVersionNum(StringUtil.getVersionNum(0, versionId, 0, 0));
                        itemService.save(item);
                        return new ConfigResponse(HttpStatus.OK.value(), 1, "上传成功！");
                    } else {
                        return new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "请先添加对应主版本！");
                    }
                }
            } else {
                int num = 0;
                int uploadCount = 0;
                int noMainCount = 0;
                int keyPubCount = 0;
                int lineNum = 0;
                Set<String> propertyNames = prop.stringPropertyNames();
                for (String name : propertyNames) {
                    if (name.startsWith("pub.")) {
                        keyPubCount++;
                    } else {
                        name = StringUtil.getNoOtherLanguage(name);
                        name = StringUtil.getNoStartNumStr(name);
                        Map<String, Object> itemCountMap = new HashMap<>(16);
                        itemCountMap.put("instanceId", instanceId);
                        itemCountMap.put("key", name);
                        itemCountMap.put("resourceId", resourceId);
                        lineNum++;
                        int itemCount = itemService.countByMap(itemCountMap);
                        String value = prop.getProperty(name);
                        if (itemCount < 1) {
                            item.setId(StringUtil.getId() + num++);
                            item.setKey(name);
                            item.setValue(value);
                            item.setVersionNum(StringUtil.getVersionNum(0, versionId, 0, num++));
                            item.setLineNum(lineNum);
                            if (versionId == 0) {
                                itemService.save(item);
                                uploadCount++;
                            } else {
                                //主版本里面要有可以才能添加
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
                if (keyPubCount > 0) {
                    message += "，" + keyPubCount + "条键以pub.开头";
                }
                if (uploadCount < size) {
                    return new ConfigResponse(HttpStatus.OK.value(), uploadCount, message);
                } else {
                    return new ConfigResponse(HttpStatus.OK.value(), size, "上传成功！");
                }
            }
        } catch (Exception e) {
            log.error("上传失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "上传失败！请联系管理员！");
        }
    }

    /**
     * @param request  http请求
     * @param response http响应
     * @param username 用户名
     * @return void
     * @description 下载项目配置数据
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
        long versionId = Long.valueOf(request.getParameter("versionId"));
        String[] itemIds = request.getParameterValues("itemIds[]");
        String[] tempIds = request.getParameter("productModuleEnvId").split("_");
        long productId = Long.valueOf(tempIds[0]);
        long moduleId = Long.valueOf(tempIds[1]);
        long envId = Long.valueOf(tempIds[2]);
        InstanceType instanceType = instanceTypeService.get(instanceTypeId);
        //文件名为项目名称+模块名称+环境名称+版本+时间戳+后缀名
        Product product = productService.get(productId);
        Module module = moduleService.get(moduleId);
        Env env = envService.get(envId);
        String version = versionId == 0 ? "无" : "灰度版本";
        String timeFormat = DateUtil.timestamp2Date(System.currentTimeMillis(), "yyyy-MM-dd-HH");
        String fileName = "-(" + timeFormat + ")." + instanceType.getName();
        try {
            fileName = java.net.URLEncoder.encode(product.getName() + "-" + module.getName() + "-" + env.getName()
                                                  + "-" + version, "utf-8") + fileName;
        } catch (UnsupportedEncodingException e) {
            log.error("不支持的编码格式", e);
        }
        if (fileName != null) {
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            byte[] buffer = new byte[1024];
            InputStream fis = null;
            BufferedInputStream bis = null;
            List<Map<String, Object>> listMap;
            String isAllDownload = request.getParameter("isAllDownload");
            if (StringUtils.isNotBlank(isAllDownload)) {
                listMap = instanceService.getAllKeyAndValue(request, username);
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
     * @param hsr      http请求
     * @param username 用户名
     * @return java.lang.Object 查询结果
     * @description 获取改条件下所有的配置信息
     * @author maodi
     * @createDate 2018/8/31 14:23
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_all_key_and_value")
    public @ResponseBody
    Object getAllKeyAndValue(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            return instanceService.getAllKeyAndValue(hsr, username);
        } catch (Exception e) {
            log.error("获取项目properties数据出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取项目properties数据出错");
        }
    }

    /**
     * @param ids      条目id数组
     * @param username 用户名
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
                    .getFrontVersion(1, versionId, 0)));
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "发布成功！");
        } catch (Exception e) {
            log.error("根据ids发布项目配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids发布项目配置出错");
        }
    }

    /**
     * @param ids       条目id数组
     * @param versionId 版本
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
                itemService.save(item);
                long[] appIds = itemAppService.getAppIdsByItemId(oldItemId);
                if (appIds != null && appIds.length > 0) {
                    itemAppSave(appIds, newItemId);
                }
                long[] itemIds = {newItemId};
                itemAppService.overByItemIds(itemIds);
            }
            itemService.offlineByIds(StringUtil.actionMapFrontVersion(ids, userService.getIdByName(username), StringUtil
                    .getFrontVersion(0, versionId, 0)));
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "下线成功！");
        } catch (Exception e) {
            log.error("根据ids下线项目配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids下线项目配置出错");
        }
    }

    /**
     * @param map 处理的map
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 处理过后的map
     * @description 处理产生重复的消息
     * @author maodi
     * @createDate 2018/8/31 14:24
     */
    private ConfigResponse getDuplicateInfo(Map<String, Object> map) {
        String duplicateInfo = "您输入的内容不能与公共配置重复";
        int currentCount = itemService.duplicateCurrentCountByMap(map);
        if (currentCount > 0) {
            duplicateInfo = "您输入的内容不能与当前项目配置重复";
        }
        int publicCount = itemService.duplicatePublicCountByMap(map);
        if (publicCount > 0) {
            duplicateInfo = "您输入的内容不能与公共配置重复";
        }
        return new ConfigResponse(HttpStatus.CONFLICT.value(), 0, duplicateInfo);
    }

    /**
     * @param map 处理的map
     * @return java.lang.String 处理过后的map
     * @description 获取重复的是public还是product
     * @author maodi
     * @createDate 2018/8/31 14:25
     */
    private String getCopyDuplicateInfo(Map<String, Object> map) {
        String duplicateInfo = "0";
        int currentCount = itemService.duplicateCurrentCountByMap(map);
        if (currentCount > 0) {
            duplicateInfo = "1";
        }
        int publicCount = itemService.duplicatePublicCountByMap(map);
        if (publicCount > 0) {
            duplicateInfo = "0";
        }
        return duplicateInfo;
    }

    /**
     * @param sbNoMain     没有主版本的信息
     * @param sbPublic     公共配置重复信息
     * @param sbProduct    项目重复信息
     * @param successCount 成功的数量
     * @return java.lang.String 处理复制消息过后的消息
     * @description 处理复制消息产生的重复信息
     * @author maodi
     * @createDate 2018/8/31 14:25
     */
    private String dealMessage(StringBuilder sbNoMain, StringBuilder sbPublic, StringBuilder sbProduct, long
            successCount) {
        String message;
        if (sbNoMain.length() > 0) {
            sbNoMain = new StringBuilder(sbNoMain.substring(0, sbNoMain.length() - 1));
        }
        if (sbPublic.length() > 0) {
            sbPublic = new StringBuilder(sbPublic.substring(0, sbPublic.length() - 1));
        }
        if (sbProduct.length() > 0) {
            sbProduct = new StringBuilder(sbProduct.substring(0, sbProduct.length() - 1));
        }
        if (sbNoMain.length() < 1 && sbProduct.length() < 1 && sbPublic.length() < 1) {
            message = "复制成功!";
        } else {
            if (successCount > 0) {
                message = "部分复制成功";
            } else {
                message = "复制失败";
            }
            if (sbNoMain.length() > 0) {
                message += "," + sbNoMain + "没有对应主版本";
            }
            if (sbPublic.length() > 0) {
                message += "," + sbPublic + "与公共配置重复";
            }
            if (sbProduct.length() > 0) {
                message += "," + sbProduct + "与当前项目配置重复";
            }
        }
        return message;
    }

}
