package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Module;
import com.hsgene.hdas.cmcs.modules.admin.domain.ModuleProduct;
import com.hsgene.hdas.cmcs.modules.admin.domain.ModuleProducts;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 模块项目控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/5/29 15:04
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/module_product")
public class ModuleProductController {

    @Autowired
    private IEnvModuleProductService envModuleProductService;

    @Autowired
    private IModuleProductService moduleProductService;

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IEnvService envService;

    @Autowired
    private IAuthResourceService authResourceService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IRoleAuthResourceService roleAuthResourceService;

    @Autowired
    private IItemResourceService itemResourceService;

    /**
     * @param
     * @return java.lang.String 模块新增页名字
     * @description 获取模块新增页
     * @author maodi
     * @createDate 2018/6/13 15:04
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/module_product_add";
    }

    /**
     * @param
     * @return java.lang.String  模块修改页名字
     * @description 获取模块修改页
     * @author maodi
     * @createDate 2018/6/13 15:05
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/module_product_update";
    }

    /**
     * @param moduleProducts 模块-项目组
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 新增结果
     * @description 新增模块
     * @author maodi
     * @createDate 2018/6/13 15:07
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(ModuleProducts moduleProducts) {
        ConfigResponse configResponse;
        try {
            long moduleId = StringUtil.getId();
            Map<String, Object> moduleMap = new HashMap<>(16);
            moduleMap.put("name", moduleProducts.getName());
            int moduleNum = moduleService.countByMap(moduleMap);
            if (moduleNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "添加失败！跟其它模块重复");
            } else {
                Module module = new Module();
                module.setId(moduleId);
                module.setDescription(moduleProducts.getDescription());
                module.setName(moduleProducts.getName());
                Timestamp timestamp = StringUtil.getNowTimestamp();
                module.setCreateDateTime(timestamp);
                module.setUpdateDateTime(timestamp);
                moduleService.save(module);
                long[] productIds = moduleProducts.getProductIds();
                action(moduleId, productIds);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("新增模块出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增模块出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object  模块分页数据
     * @description 根据名字获取模块分页数据
     * @author maodi
     * @createDate 2018/6/13 15:27
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByNamePage(HttpServletRequest hsr) {
        try {
            return moduleProductService.selectByNamePage(hsr);
        } catch (Exception e) {
            log.error("根据名字获取模块分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据名字获取模块分页数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 模块分页数据
     * @description 获取模块分页数据
     * @author maodi
     * @createDate 2018/6/13 15:28
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            return moduleProductService.selectByPage(hsr);
        } catch (NumberFormatException e) {
            log.error("获取模块分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取模块分页数据出错");
        }
    }

    /**
     * @param moduleProducts 模块-项目组
     * @return java.lang.Object 修改结果
     * @description 修改模块
     * @author maodi
     * @createDate 2018/6/13 15:29
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(ModuleProducts moduleProducts) {
        ConfigResponse configResponse;
        try {
            long moduleId = moduleProducts.getId();
            Map<String, Object> moduleMap = new HashMap<>(16);
            moduleMap.put("name", moduleProducts.getName());
            moduleMap.put("id", moduleId);
            int moduleNum = moduleService.countByMap(moduleMap);
            if (moduleNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其它模块重复");
            } else {
                Module module = new Module();
                module.setId(moduleId);
                module.setDescription(moduleProducts.getDescription());
                module.setName(moduleProducts.getName());
                moduleService.update(module);
                long[] ids = {moduleId};
                moduleProductService.deleteByModuleIds(ids);
                long[] productIds = moduleProducts.getProductIds();
                envModuleProductService.deleteByModuleIdAndNotInProductIds(moduleId, productIds);
                resourceService.deleteByModuleIdAndNotInProductIds(moduleId, productIds);
                action(moduleId, productIds);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改模块出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改模块出错");
        }
        return configResponse;
    }

    /**
     * @param ids 模块ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除模块
     * @author maodi
     * @createDate 2018/6/13 15:30
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            moduleService.deleteByIds(ids);
            //module删除对应的ModuleProduct也要删除
            moduleProductService.deleteByModuleIds(ids);
            //module删除，对应的EnvModuleProduct也要删除
            envModuleProductService.deleteByModuleIds(ids);
            //EnvModuleProduct中module删除完，删除env
            envService.deleteNotInIds(envModuleProductService.getDistinctEnvIds());
            authResourceService.deleteByModuleIds(ids);
            roleAuthResourceService.deleteByModuleIds(ids);
            resourceService.deleteByModuleIds(ids);
            itemResourceService.deleteByModuleIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除模块", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除模块");
        }
    }

    /**
     * @param moduleId   模块id
     * @param productIds 项目ids
     * @return void
     * @description 批量新增模块数据
     * @author maodi
     * @createDate 2018/6/13 15:31
     */
    private void action(long moduleId, long[] productIds) {
        int num = 0;
        for (long productId : productIds) {
            long id = StringUtil.getId() + num++;
            ModuleProduct moduleProduct = new ModuleProduct();
            moduleProduct.setId(id);
            moduleProduct.setModuleId(moduleId);
            moduleProduct.setProductId(productId);
            moduleProductService.save(moduleProduct);
        }
    }

}
