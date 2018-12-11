package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hsgene.hdas.cmcs.modules.admin.domain.*;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description: 环境模块项目控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/5/29 15:04
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/env_module_product")
public class EnvModuleProductController {

    @Autowired
    private IEnvModuleProductService envModuleProductService;

    @Autowired
    private IModuleProductService moduleProductService;

    @Autowired
    private IEnvService envService;

    @Autowired
    private IAuthResourceService authResourceService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IVersionService versionService;

    @Autowired
    private IRoleAuthResourceService roleAuthResourceService;

    @Autowired
    private IItemResourceService itemResourceService;

    /**
     * @param
     * @return java.lang.String 环境新增页名字
     * @description 获取环境新增页
     * @author maodi
     * @createDate 2018/6/13 15:04
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/env_module_product_add";
    }

    /**
     * @param
     * @return java.lang.String  环境修改页名字
     * @description 获取环境修改页
     * @author maodi
     * @createDate 2018/6/13 15:05
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/env_module_product_update";
    }

    /**
     * @param envModulesProducts 环境-模块-项目组
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 新增结果
     * @description 新增环境
     * @author maodi
     * @createDate 2018/6/13 15:07
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(@RequestBody JSONObject envModulesProducts) {
        ConfigResponse configResponse;
        try {
            long envId = StringUtil.getId();
            String name = envModulesProducts.getString("name");
            String description = envModulesProducts.getString("description");
            JSONObject productModuleJson = envModulesProducts.getJSONObject("productModuleJson");
            Map<String, Object> envMap = new HashMap<>(16);
            envMap.put("name", name);
            int envNum = envService.countByMap(envMap);
            if (envNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "添加失败！跟其它环境重复");
            } else {
                Env env = new Env();
                env.setId(envId);
                env.setDescription(description);
                env.setName(name);
                env.setCreateDateTime(StringUtil.getNowTimestamp());
                envService.save(env);
                action(envId, productModuleJson);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("添加环境出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "添加环境出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object  环境分页数据
     * @description 根据名字获取环境分页数据
     * @author maodi
     * @createDate 2018/6/13 15:27
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByNamePage(HttpServletRequest hsr) {
        try {
            return envModuleProductService.selectByNamePage(hsr);
        } catch (Exception e) {
            log.error("根据名字获取环境分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据名字获取环境分页数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 环境分页数据
     * @description 获取环境分页数据
     * @author maodi
     * @createDate 2018/6/13 15:28
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            return envModuleProductService.selectByPage(hsr);
        } catch (NumberFormatException e) {
            log.error("获取环境分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取环境分页数据出错");
        }
    }

    /**
     * @param envModulesProducts 环境-模块-项目组
     * @return java.lang.Object 修改结果
     * @description 修改环境
     * @author maodi
     * @createDate 2018/6/13 15:29
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(@RequestBody JSONObject envModulesProducts) {
        ConfigResponse configResponse;
        try {
            long envId = envModulesProducts.getLong("id");
            String name = envModulesProducts.getString("name");
            String description = envModulesProducts.getString("description");
            JSONObject productModuleJson = envModulesProducts.getJSONObject("productModuleJson");
            Map<String, Object> envMap = new HashMap<>(16);
            envMap.put("name", name);
            envMap.put("id", envId);
            int envNum = envService.countByMap(envMap);
            if (envNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其它环境重复");
            } else {
                Env env = new Env();
                env.setId(envId);
                env.setDescription(description);
                env.setName(name);
                envService.update(env);
                long[] ids = {envId};
                envModuleProductService.deleteByEnvIds(ids);
                resourceService.deleteByEnvIds(ids);
                action(envId, productModuleJson);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改环境出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改环境出错");
        }
        return configResponse;
    }

    /**
     * @param ids 环境ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除环境
     * @author maodi
     * @createDate 2018/6/13 15:30
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            envService.deleteByIds(ids);
            envModuleProductService.deleteByEnvIds(ids);
            authResourceService.deleteByEnvIds(ids);
            roleAuthResourceService.deleteByEnvIds(ids);
            resourceService.deleteByEnvIds(ids);
            itemResourceService.deleteByEnvIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除环境", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除环境");
        }
    }

    /**
     * @param hsr http请求
     * @return java.lang.Object 获取应用范围数据的结果
     * @description 获取应用范围的数据
     * @author maodi
     * @createDate 2018/8/23 15:04
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/product_module_data")
    public @ResponseBody
    Object getProductModuleData(HttpServletRequest hsr) {
        try {
            int draw = Integer.valueOf(StringUtils.isBlank(hsr.getParameter("draw")) ? "1" : hsr.getParameter
                    ("draw"));
            long envId = Long.valueOf(StringUtils.isBlank(hsr.getParameter("envId")) ? "-1" : hsr.getParameter
                    ("envId"));
            Map<String, Object> map = new HashMap<>(16);
            map.put("envId", envId);
            dealEnvProductModuleData(hsr, map);
            List<Map<String, Object>> listMap = moduleProductService.getProductModuleData(map);
            return PageInfoUtil.dealListMap(listMap, draw);
        } catch (Exception e) {
            log.error("获取公共配置实例项目环境数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取公共配置实例项目环境数据出错");
        }
    }

    /**
     * @param envId             环境id
     * @param productModuleJson 模块项目Json
     * @return void
     * @description 批量新增环境数据
     * @author maodi
     * @createDate 2018/6/13 15:31
     */
    private void action(long envId, JSONObject productModuleJson) {
        int num1 = 0;
        int num2 = 0;
        Set<String> keySet = productModuleJson.keySet();
        for (String productIdStr : keySet) {
            long productId = Long.valueOf(productIdStr);
            JSONArray moduleArray = productModuleJson.getJSONArray(productIdStr);
            for (int i = 0, length = moduleArray.size(); i < length; i++) {
                long moduleId = moduleArray.getLong(i);
                EnvModuleProduct emp = new EnvModuleProduct();
                long id1 = StringUtil.getId() + num1++;
                emp.setId(id1);
                emp.setProductId(productId);
                emp.setModuleId(moduleId);
                emp.setEnvId(envId);
                envModuleProductService.save(emp);
                Resource resource = new Resource();
                resource.setId(StringUtil.getId());
                resource.setProductId(productId);
                resource.setModuleId(moduleId);
                resource.setEnvId(envId);
                List<Version> versions = versionService.getAll();
                for (Version version : versions) {
                    long id2 = StringUtil.getId() + num2++;
                    long versionId = version.getId();
                    resource.setId(id2);
                    resource.setVersionId(versionId);
                    resourceService.save(resource);
                }
            }
        }
    }

    /**
     * @param hsr http请求
     * @param map 查询条件的map
     * @return void
     * @description 处理环境中选中项目模块搜索条件数据
     * @author maodi
     * @createDate 2018/9/17 10:19
     */
    public void dealEnvProductModuleData(HttpServletRequest hsr, Map<String, Object> map) {
        String moduleIdStr = hsr.getParameter("moduleId");
        String productName = hsr.getParameter("name");
        String isSelectStr = hsr.getParameter("configId");
        long moduleId;
        long isSelect;
        if (Constant.UNDEFINED.equals(moduleIdStr)) {
            moduleId = -1;
        } else {
            moduleId = StringUtils.isBlank(moduleIdStr) ? -1 : Long.valueOf(moduleIdStr);
        }
        if (Constant.UNDEFINED.equals(isSelectStr)) {
            isSelect = -1;
        } else {
            isSelect = StringUtils.isBlank(isSelectStr) ? -1 : Long.valueOf(isSelectStr);
        }
        if (moduleId != -1) {
            map.put("moduleId", moduleId);
        }
        if (isSelect != -1) {
            map.put("isSelect", isSelect);
        }
        if (!Constant.UNDEFINED.equals(productName) && StringUtils.isNoneBlank(productName)) {
            map.put("productName", productName);
        }
    }

}
