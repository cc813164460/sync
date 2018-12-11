package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsgene.hdas.cmcs.modules.admin.domain.*;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.*;
import com.hsgene.hdas.cmcs.modules.admin.util.PageInfoUtil;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * @description: 角色权限资源控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/role_auth_resource")
public class RoleAuthResourceController {

    @Autowired
    private IRoleAuthResourceService roleAuthResourceService;

    @Autowired
    private IAuthResourceService authResourceService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IRoleClassService roleClassService;

    /**
     * @param
     * @return java.lang.String  角色权限资源新增页名字
     * @description 获取角色权限资源新增页
     * @author maodi
     * @createDate 2018/6/13 15:38
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/role_auth_resource_add";
    }

    /**
     * @param
     * @return java.lang.String 角色权限资源修改页名字
     * @description 获取角色权限资源修改页
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/role_auth_resource_update";
    }

    /**
     * @param json 角色权限资源信息
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse   新增结果
     * @description 新增角色权限资源
     * @author maodi
     * @createDate 2018/6/13 15:41
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(@RequestBody JSONObject json) {
        ConfigResponse configResponse;
        try {
            long roleId = json.getLong("roleId");
            long classId = json.getLong("classId");
            Map<String, Object> map = new HashMap<>(16);
            map.put("roleId", roleId);
            int count = roleAuthResourceService.countByMap(map);
            if (count > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 0, "添加失败！该角色权限已经存在！");
            } else {
                RoleClass roleClass = new RoleClass();
                roleClass.setId(StringUtil.getId());
                roleClass.setRoleId(roleId);
                roleClass.setClassId(classId);
                roleClassService.save(roleClass);
                int num = arrayAction(json.getJSONArray("data"), roleId);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), num, "添加成功！");
            }
        } catch (Exception e) {
            log.error("新增角色权限资源信息出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增角色权限资源信息出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 权限管理分页数据
     * @description 根据名字获取权限管理分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByNamePage(HttpServletRequest hsr) {
        try {
            return authResourceService.selectByNamePage(hsr);
        } catch (Exception e) {
            log.error("根据名字获取权限管理分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据名字获取权限管理分页数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 权限管理分页数据
     * @description 获取权限管理分页数据
     * @author maodi
     * @createDate 2018/6/13 15:41
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            PageInfo<Map<String, Object>> datas = roleAuthResourceService.selectByPage(hsr);
            return datas;
        } catch (NumberFormatException e) {
            log.error("获取权限管理分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取权限管理分页数据出错");
        }
    }

    /**
     * @param hsr http请求
     * @return java.lang.Object   添加和修改页面中权限的数据
     * @description 获取添加和修改页面中权限的数据
     * @author maodi
     * @createDate 2018/9/7 16:07
     */
    @RequestMapping("/query_action")
    public @ResponseBody
    Object queryActionByPage(HttpServletRequest hsr) {
        try {
            int draw = Integer.valueOf(hsr.getParameter("draw"));
            Map<String, Object> map = new HashMap<>(16);
            if (hsr.getParameter("roleId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("roleId").toString())) {
                long roleId = Long.valueOf(hsr.getParameter("roleId"));
                map.put("roleId", roleId);
            }
            List<Map<String, Object>> listMap = roleAuthResourceService.getSelectByMap(map);
            return PageInfoUtil.dealListMap(listMap, draw);
        } catch (NumberFormatException e) {
            log.error("获取权限管理分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取权限管理分页数据出错");
        }
    }

    /**
     * @param hsr http请求
     * @return java.lang.Object  添加和修改页面中权限的数据
     * @description 根据条件获取添加和修改页面中权限的数据
     * @author maodi
     * @createDate 2018/9/7 16:08
     */
    @RequestMapping("/query_action_by_condition")
    public @ResponseBody
    Object queryActionByConditionPage(HttpServletRequest hsr) {
        try {
            int draw = Integer.valueOf(hsr.getParameter("draw"));
            Map<String, Object> map = new HashMap<>(16);
            if (hsr.getParameter("roleId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("roleId").toString())) {
                long roleId = Long.valueOf(hsr.getParameter("roleId"));
                map.put("roleId", roleId);
            }
            String name = hsr.getParameter("name");
            if (StringUtils.isNotBlank(name)) {
                map.put("name", name);
            }
            if (hsr.getParameter("moduleId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("moduleId").toString())) {
                long moduleId = Long.valueOf(hsr.getParameter("moduleId"));
                if (moduleId != -1) {
                    map.put("moduleId", moduleId);
                }
            }
            if (hsr.getParameter("envId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("envId").toString())) {
                long envId = Long.valueOf(hsr.getParameter("envId"));
                if (envId != -1) {
                    map.put("envId", envId);
                }
            }
            if (hsr.getParameter("organId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("organId").toString())) {
                long organId = Long.valueOf(hsr.getParameter("organId"));
                if (organId != -1) {
                    map.put("organId", organId);
                }
            }
            if (hsr.getParameter("areaId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("areaId").toString())) {
                long areaId = Long.valueOf(hsr.getParameter("areaId"));
                if (areaId != -1) {
                    map.put("areaId", areaId);
                }
            }
            if (hsr.getParameter("configId") != null && !Constant.UNDEFINED.equals(hsr.getParameter("configId").toString())) {
                long configId = Long.valueOf(hsr.getParameter("configId"));
                if (configId != -1) {
                    map.put("configId", configId);
                }
            }
            List<Map<String, Object>> listMap = roleAuthResourceService.getSelectByCondition(map);
            return PageInfoUtil.dealListMap(listMap, draw);
        } catch (NumberFormatException e) {
            log.error("获取权限管理分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取权限管理分页数据出错");
        }
    }

    /**
     * @param json 角色权限资源信息
     * @return java.lang.Object  修改结果
     * @description 修改角色权限资源
     * @author maodi
     * @createDate 2018/6/13 15:42
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(@RequestBody JSONObject json) {
        ConfigResponse configResponse;
        try {
            long roleId = json.getLong("roleId");
            Map<String, Object> map = new HashMap<>(16);
            map.put("roleId", roleId);
            //查询原来改role的json
            List<Map<String, Object>> listMap = roleAuthResourceService.getSelectByMap(map);
            JSONArray nowData = json.getJSONArray("data");
            //获取所有现在的resourceId
            Set<Long> nowSet = new HashSet<>();
            for (int i = 0, size = nowData.size(); i < size; i++) {
                nowSet.add(nowData.getJSONObject(i).getLong("resourceId"));
            }
            for (Map<String, Object> tempMap : listMap) {
                long oldResourceId = Long.valueOf(tempMap.get("id").toString());
                //现在的resourceId没有原来的resourceId，就将原来的resourceId的内容添加到现在里面
                if (!nowSet.contains(oldResourceId)) {
                    JSONObject tempJson = new JSONObject();
                    tempJson.put("resourceId", oldResourceId);
                    tempJson.put("select", Integer.valueOf(tempMap.get("select").toString()));
                    tempJson.put("insert", Integer.valueOf(tempMap.get("insert").toString()));
                    tempJson.put("update", Integer.valueOf(tempMap.get("update").toString()));
                    tempJson.put("delete", Integer.valueOf(tempMap.get("delete").toString()));
                    tempJson.put("release", Integer.valueOf(tempMap.get("release").toString()));
                    tempJson.put("offline", Integer.valueOf(tempMap.get("offline").toString()));
                    nowData.add(tempJson);
                }
            }
            long classId = json.getLong("classId");
            long[] ids = {roleId};
            long[] arIds = roleAuthResourceService.getArIdsByRoleIds(ids);
            sysPermissionService.deleteByRoleIds(ids);
            roleAuthResourceService.deleteByRoleIds(ids);
            if (arIds.length > 0) {
                authResourceService.deleteByIds(arIds);
            }
            RoleClass roleClass = new RoleClass();
            roleClass.setRoleId(roleId);
            roleClass.setClassId(classId);
            long id = roleClassService.getIdByRoleId(roleId);
            if (id == -1) {
                id = StringUtil.getId();
                roleClass.setId(id);
                roleClassService.save(roleClass);
            } else {
                roleClass.setId(id);
                roleClassService.update(roleClass);
            }
            int num = arrayAction(nowData, roleId);
            configResponse = new ConfigResponse(HttpStatus.OK.value(), num, "修改成功！");
        } catch (Exception e) {
            log.error("修改角色权限资源信息出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改角色权限资源信息出错");
        }
        return configResponse;
    }

    /**
     * @param ids 角色权限资源ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除角色权限资源
     * @author maodi
     * @createDate 2018/6/13 15:42
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            roleClassService.deleteByRoleIds(ids);
            long[] arIds = roleAuthResourceService.getArIdsByRoleIds(ids);
            roleAuthResourceService.deleteByRoleIds(ids);
            authResourceService.deleteByIds(arIds);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除角色权限资源出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除角色权限资源出错");
        }
    }

    /**
     * @param username 用户名
     * @return java.lang.Object  各个资源的权限
     * @description 根据用户获取各个资源的权限
     * @author maodi
     * @createDate 2018/9/7 16:09
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_user_auth_resource")
    public @ResponseBody
    Object getUserAuthResource(@SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            Map<String, Object> map = new HashMap<>(16);
            boolean isAdmin = false;
            if (userId == 0) {
                isAdmin = true;
            }
            map.put("is_admin", isAdmin);
            map.put("resources", dealResource(resourceService.getResource()));
            Map<String, Object> queryMap = new HashMap<>(16);
            queryMap.put("userId", userId);
            map.put("data", roleAuthResourceService.getUserAuthResource(queryMap));
            return map;
        } catch (Exception e) {
            log.error("获取人员权限资源信息出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取人员权限资源信息出错");
        }
    }

    /**
     * @param array  json数据
     * @param roleId 角色的id
     * @return int 成功的个数
     * @description 处理插入和修改成功的个数
     * @author maodi
     * @createDate 2018/9/7 16:10
     */
    private int arrayAction(JSONArray array, long roleId) {
        int num = 0;
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            if (obj.getInteger("select") == 1) {
                long authId = authService.getIdByName("查看");
                action(num++, authId, obj.getLong("resourceId"), roleId);
            }
            if (obj.getInteger("insert") == 1) {
                long authId = authService.getIdByName("添加");
                action(num++, authId, obj.getLong("resourceId"), roleId);
            }
            if (obj.getInteger("update") == 1) {
                long authId = authService.getIdByName("编辑");
                action(num++, authId, obj.getLong("resourceId"), roleId);
            }
            if (obj.getInteger("delete") == 1) {
                long authId = authService.getIdByName("删除");
                action(num++, authId, obj.getLong("resourceId"), roleId);
            }
            if (obj.getInteger("release") == 1) {
                long authId = authService.getIdByName("发布");
                action(num++, authId, obj.getLong("resourceId"), roleId);
            }
            if (obj.getInteger("offline") == 1) {
                long authId = authService.getIdByName("下线");
                action(num++, authId, obj.getLong("resourceId"), roleId);
            }
        }
        return num;
    }

    /**
     * @param num        数字，避免id重复
     * @param authId     权限的id
     * @param resourceId 资源的id
     * @param roleId     角色的id
     * @return void
     * @description 保存静态配置和动态配置的权限
     * @author maodi
     * @createDate 2018/9/7 16:11
     */
    private void action(int num, long authId, long resourceId, long roleId) {
        long arId = StringUtil.getId() + num;
        AuthResource authResource = new AuthResource();
        authResource.setAuthId(authId);
        authResource.setId(arId);
        authResource.setResourceId(resourceId);
        authResourceService.save(authResource);
        RoleAuthResource roleAuthResource = new RoleAuthResource();
        roleAuthResource.setRoleId(roleId);
        roleAuthResource.setId(arId);
        roleAuthResource.setArId(arId);
        roleAuthResourceService.save(roleAuthResource);
        SysPermission sysPermission = new SysPermission();
        sysPermission.setId(arId);
        sysPermission.setRoleId(roleId);
        boolean isConfig = false;
        //静态配置
        for (ConfigAuthUrl cfau : Constant.CONFIG_AUTH_URL_LIST) {
            long cfauResourceId = cfau.getResourceId();
            long cfauAuthId = cfau.getAuthId();
            if (resourceId == cfauResourceId && authId == cfauAuthId) {
                String setUrl = cfau.getUrlSet().toString();
                //去除set中的首尾[]
                sysPermission.setUrl(setUrl.substring(1, setUrl.length() - 1));
                sysPermissionService.save(sysPermission);
                isConfig = true;
                //外层还有循环，只需判断这次是否满足
                break;
            }
        }
        //动态工程
        if (!isConfig) {
            Resource resource = resourceService.get(resourceId);
            long versionId = resource.getVersionId();
            String productModuleEnvId = resource.getProductId() + "_" + resource.getModuleId() + "_" + resource
                    .getEnvId();
            String addPageUrl = "/instance/add_page?productModuleEnvId=" + productModuleEnvId;
            String updatePageUrl = "/instance/update_page?productModuleEnvId=" + productModuleEnvId;
            String uploadUrl = "/instance/upload?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
            String grayAddPageUrl = "/instance/gray_add_page?productModuleEnvId=" + productModuleEnvId;
            String grayUpdatePageUrl = "/instance/gray_update_page?productModuleEnvId=" + productModuleEnvId;
            String copyConfigPageUrl = "/instance/copy_config_page?productModuleEnvId=" + productModuleEnvId;
            String grayCopyConfigPageUrl = "/instance/gray_copy_config_page?productModuleEnvId=" + productModuleEnvId;
            String insertCopyConfigUrl = "/instance/insert_copy_config?productModuleEnvId=" + productModuleEnvId +
                                         "&versionId=" + versionId;
            String insertUrl = "/instance/insert?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
            String updateUrl = "/instance/update?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
            String deleteUrl = "/instance/delete_by_ids?productModuleEnvId=" + productModuleEnvId + "&versionId=" +
                               versionId;
            String offlineUrl = "/instance/offline?productModuleEnvId=" + productModuleEnvId + "&versionId=" +
                                versionId;
            String releaseUrl = "/instance/release?productModuleEnvId=" + productModuleEnvId + "&versionId=" +
                                versionId;
            Set<String> urlSet = new HashSet<>();
            if (authId == 0) {
                setQueryAuth(urlSet, productModuleEnvId, versionId);
            } else if (authId == 1) {
                setQueryAuth(urlSet, productModuleEnvId, versionId);
                urlSet.add(insertUrl);
                urlSet.add(uploadUrl);
                urlSet.add(insertCopyConfigUrl);
                if (versionId == 0) {
                    urlSet.add(addPageUrl);
                    urlSet.add(copyConfigPageUrl);
                } else {
                    urlSet.add(grayAddPageUrl);
                    urlSet.add(grayCopyConfigPageUrl);
                }
            } else if (authId == 2) {
                setQueryAuth(urlSet, productModuleEnvId, versionId);
                urlSet.add(updateUrl);
                urlSet.add(uploadUrl);
                urlSet.add(insertCopyConfigUrl);
                if (versionId == 0) {
                    urlSet.add(updatePageUrl);
                    urlSet.add(copyConfigPageUrl);
                } else {
                    urlSet.add(grayUpdatePageUrl);
                    urlSet.add(grayCopyConfigPageUrl);
                }
            } else if (authId == 3) {
                setQueryAuth(urlSet, productModuleEnvId, versionId);
                urlSet.add(deleteUrl);
            } else if (authId == 4) {
                setQueryAuth(urlSet, productModuleEnvId, versionId);
                urlSet.add(releaseUrl);
            } else if (authId == 5) {
                setQueryAuth(urlSet, productModuleEnvId, versionId);
                urlSet.add(offlineUrl);
            }
            String setUrl = urlSet.toString();
            //去除set中的首尾[]
            sysPermission.setUrl(setUrl.substring(1, setUrl.length() - 1));
            sysPermissionService.save(sysPermission);
        }
    }

    /**
     * @param urlSet             url的set
     * @param productModuleEnvId 项目-模块-环境id
     * @param versionId          主灰版本id
     * @return void
     * @description 设置动态项目的查询权限
     * @author maodi
     * @createDate 2018/9/7 16:12
     */
    private void setQueryAuth(Set<String> urlSet, String productModuleEnvId, long versionId) {
        String pageUrl = "/instance/page?productModuleEnvId=" + productModuleEnvId;
        String selectUrl = "/instance/query?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
        String queryByKeyUrl = "/instance/query_by_name?productModuleEnvId=" + productModuleEnvId + "&versionId=" +
                               versionId;
        String queryPropertiesUrl = "/instance/query_properties?productModuleEnvId=" + productModuleEnvId +
                                    "&versionId=" + versionId;
        String excludePropertiesUrl = "/instance/query_exclude_properties?productModuleEnvId=" + productModuleEnvId +
                                      "&versionId=" + versionId;
        String downloadUrl = "/instance/download?productModuleEnvId=" + productModuleEnvId + "&versionId=" + versionId;
        String getAllKeyAndValueUrl = "/instance/get_all_key_and_value?productModuleEnvId=" + productModuleEnvId +
                                      "&versionId=" + versionId;
        urlSet.add(pageUrl);
        urlSet.add(selectUrl);
        urlSet.add(queryByKeyUrl);
        urlSet.add(queryPropertiesUrl);
        urlSet.add(excludePropertiesUrl);
        urlSet.add(downloadUrl);
        urlSet.add(getAllKeyAndValueUrl);
    }

    /**
     * @param hsr http请求
     * @param username 用户名
     * @return java.lang.Object 0为主版本，1为灰度版本
     * @description 获取是否有主版本权限，展示主版本还是灰度版本
     * @author maodi
     * @createDate 2018/6/29 10:18
     */
    @RequestMapping("/get_main_version_auth")
    public @ResponseBody
    Object getMainVersionAuth(HttpServletRequest hsr, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            if ("admin".equals(username)) {
                return 0;
            } else {
                long userId = userService.getIdByName(username);
                Map<String, Object> queryMap = new HashMap<>(16);
                queryMap.put("userId", userId);
                String isPublic = hsr.getParameter("isPublic");
                if ("true".equals(isPublic)) {
                    queryMap.put("isPublic", true);
                } else {
                    long productId = Long.valueOf(hsr.getParameter("productId"));
                    long moduleId = Long.valueOf(hsr.getParameter("moduleId"));
                    long envId = Long.valueOf(hsr.getParameter("envId"));
                    queryMap.put("productId", productId);
                    queryMap.put("moduleId", moduleId);
                    queryMap.put("envId", envId);
                }
                List<Map<String, Object>> list = roleAuthResourceService.getUserAuthResource(queryMap);
                for (Map<String, Object> map : list) {
                    String id = map.get("id").toString();
                    if ("9".equals(id)) {
                        return 0;
                    }
                    String versionName = map.get("version_name").toString();
                    if ("无".equals(versionName)) {
                        return 0;
                    }
                }
                return 1;
            }
        } catch (Exception e) {
            log.error("根据用户获取权限数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据用户获取权限数据出错");
        }
    }

    /**
     * @param username 用户名
     * @return java.lang.Object 0为主版本，1为灰度版本
     * @description 获取是否有主版本权限，展示主版本还是灰度版本
     * @author maodi
     * @createDate 2018/6/29 10:18
     */
    @RequestMapping("/get_release_status")
    public @ResponseBody
    Object getReleaseStatus(@SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            if ("admin".equals(username)) {
                return 1;
            } else {
                long userId = userService.getIdByName(username);
                Map<String, Object> queryMap = new HashMap<>(16);
                queryMap.put("userId", userId);
                List<Map<String, Object>> list = roleAuthResourceService.getUserAuthResource(queryMap);
                for (Map<String, Object> map : list) {
                    String id = map.get("id").toString();
                    if ("11".equals(id) || "13".equals(id)) {
                        return 1;
                    } else if ("12".equals(id) || "14".equals(id)) {
                        return 0;
                    }
                }
                return 1;
            }
        } catch (Exception e) {
            log.error("获取实例properties分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取实例properties分页数据出错");
        }
    }

    /**
     * @param listMap 资源权限数据
     * @return com.alibaba.fastjson.JSONObject  处理过后的资源权限数据
     * @description 处理资源权限数据
     * @author maodi
     * @createDate 2018/9/7 16:13
     */
    private JSONObject dealResource(List<Map<String, Object>> listMap) {
        JSONObject outerJson = new JSONObject();
        for (Map<String, Object> map : listMap) {
            String productIdStr = map.get("product_id").toString();
            JSONObject tempJson = outerJson.getJSONObject(productIdStr);
            if (tempJson == null) {
                tempJson = new JSONObject();
            }
            tempJson.put("productName", map.get("product_name"));
            JSONArray dataArray = tempJson.getJSONArray("data");
            if (dataArray == null) {
                dataArray = new JSONArray();
            }
            JSONObject innerJson = new JSONObject();
            innerJson.put("moduleId", map.get("module_id"));
            innerJson.put("moduleName", map.get("module_name"));
            innerJson.put("envIds", map.get("env_ids"));
            innerJson.put("envNames", map.get("env_names"));
            dataArray.add(innerJson);
            tempJson.put("data", dataArray);
            outerJson.put(productIdStr, tempJson);
        }
        return outerJson;
    }

}
