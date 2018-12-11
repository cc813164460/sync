package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Product;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.*;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 项目控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/5/29 15:04
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/product")
public class ProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IAuthResourceService authResourceService;

    @Autowired
    private IEnvModuleProductService envModuleProductService;

    @Autowired
    private IModuleProductService moduleProductService;

    @Autowired
    private IRoleAuthResourceService roleAuthResourceService;

    @Autowired
    private IEnvService envService;

    @Autowired
    private IModuleService moduleService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private IItemResourceService itemResourceService;

    /**
     * @param
     * @return java.lang.String 项目新增页名字
     * @description 获取项目新增页
     * @author maodi
     * @createDate 2018/6/13 14:55
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/product_add";
    }

    /**
     * @param
     * @return java.lang.String 项目修改页名字
     * @description 获取项目修改页
     * @author maodi
     * @createDate 2018/6/13 14:55
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/product_update";
    }

    /**
     * @param product 项目
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 新增结果
     * @description 新增项目
     * @author maodi
     * @createDate 2018/6/13 14:56
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(Product product) {
        ConfigResponse configResponse;
        try {
            long id = StringUtil.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", product.getName());
            int num = productService.countByMap(map);
            if (num > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "添加失败！跟其他项目名重复");
            } else {
                product.setId(id);
                productService.save(product);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("新增项目出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增项目出错");
        }
        return configResponse;
    }

    /**
     * @param areaId  分布id
     * @param organId 部门id
     * @param userId  用户id
     * @param isAll   是否全部数据
     * @return java.lang.Object 项目数据，id、name的键值对组成的链表
     * @description 根据分布id，部门id，用户id获取项目数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 14:57
     */
    @RequestMapping("/product_data")
    public @ResponseBody
    Object getSelectByMap(@RequestParam(value = "areaId", required = false, defaultValue = "-1") long areaId,
                          @RequestParam(value = "organId", required = false, defaultValue = "-1") long organId,
                          @RequestParam(value = "userId", required = false, defaultValue = "-1") long userId,
                          @RequestParam(value = "isAll", required = false, defaultValue = "0") int isAll) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            if (isAll == 1) {
                return productService.getSelectByMap();
            } else if (areaId == -1) {
                return productService.getData();
            } else if (organId == -1) {
                map.put("areaId", areaId);
            } else if (userId == -1) {
                map.put("areaId", areaId);
                map.put("organId", organId);
            } else {
                map.put("areaId", areaId);
                map.put("organId", organId);
                map.put("userId", userId);
            }
            return productService.getSelectByMap(map);
        } catch (Exception e) {
            log.error("根据分布id，部门id，用户id获取项目数据，为下拉框提供数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据分布id，部门id，用户id获取项目数据，为下拉框提供数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 项目分页数据
     * @description 根据名字获取项目分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByNamePage(HttpServletRequest hsr) {
        try {
            return productService.selectByNamePage(hsr);
        } catch (Exception e) {
            log.error("根据名字获取项目分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据名字获取项目分页数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object  项目分页数据
     * @description 获取项目分页数据
     * @author maodi
     * @createDate 2018/6/13 15:00
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            return productService.selectByPage(hsr);
        } catch (NumberFormatException e) {
            log.error("获取项目分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取项目分页数据出错");
        }
    }

    /**
     * @param product 项目
     * @return java.lang.Object  修改结果
     * @description 修改项目
     * @author maodi
     * @createDate 2018/6/13 15:03
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(Product product) {
        ConfigResponse configResponse;
        try {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", product.getId());
            map.put("name", product.getName());
            int num = productService.countByMap(map);
            if (num > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其他项目名重复");
            } else {
                productService.update(product);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改项目出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改项目出错");
        }
        return configResponse;
    }

    /**
     * @param ids 项目ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除项目
     * @author maodi
     * @createDate 2018/6/13 15:03
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            productService.deleteByIds(ids);
            //product删除对应的ModuleProduct也要删除
            moduleProductService.deleteByProductIds(ids);
            //product删除对应的EnvModuleProduct也要删除
            envModuleProductService.deleteByProductIds(ids);
            //ModuleProduct中删除完product,删除module
            moduleService.deleteNotInIds(moduleProductService.getDistinctModuleIds());
            //EnvModuleProduct中删除完product,product对应module在EnvModuleProduct中,根据product删除，就可以删除对应的env
            envService.deleteNotInIds(envModuleProductService.getDistinctEnvIds());
            authResourceService.deleteByProductIds(ids);
            roleAuthResourceService.deleteByProductIds(ids);
            resourceService.deleteByProductIds(ids);
            itemResourceService.deleteByProductIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除项目出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除项目出错");
        }
    }

}
