package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Area;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IAreaService;
import com.hsgene.hdas.cmcs.modules.admin.service.IOrganAreaService;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserAreaService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 分布控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/area")
public class AreaController {

    @Autowired
    private IAreaService areaService;

    @Autowired
    private IOrganAreaService organAreaService;

    @Autowired
    private IUserAreaService userAreaService;

    /**
     * @param
     * @return java.lang.String 分布新增页名字
     * @description 获取分布新增页
     * @author maodi
     * @createDate 2018/6/13 14:29
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/area_add";
    }

    /**
     * @param
     * @return java.lang.String 分布修改页名字
     * @description 获取分布修改页
     * @author maodi
     * @createDate 2018/6/13 14:30
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/area_update";
    }

    /**
     * @param
     * @return java.lang.Object 分布数据，id、name的键值对组成的链表
     * @description 获取分布数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 14:30
     */
    @RequestMapping("/area_data")
    public @ResponseBody
    Object getSelectByMap() {
        try {
            return areaService.getSelectByMap();
        } catch (Exception e) {
            log.error("获取分布下拉框数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取分布下拉框数据出错");
        }
    }

    /**
     * @param area 分布
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse  新增的结果
     * @description 新增分布
     * @author maodi
     * @createDate 2018/6/13 14:34
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(Area area) {
        ConfigResponse configResponse;
        try {
            String name = area.getName();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", name);
            int num1 = areaService.countByMap(map);
            if (num1 > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "添加失败！跟其它分布名重复");
            } else {
                area.setId(StringUtil.getId());
                area.setCreateDateTime(StringUtil.getNowTimestamp());
                areaService.save(area);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("新增分布出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增分布出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 分布分页数据
     * @description 获取分布分页数据
     * @author maodi
     * @createDate 2018/6/13 14:34
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            return areaService.selectByPage(hsr);
        } catch (NumberFormatException e) {
            log.error("获取分布分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取分布分页数据出错");
        }
    }

    /**
     * @param area 分布
     * @return java.lang.Object  修改的结果
     * @description 修改分布
     * @author maodi
     * @createDate 2018/6/13 14:35
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(Area area) {
        ConfigResponse configResponse;
        try {
            String name = area.getName();
            long id = area.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", name);
            map.put("id", id);
            int organNum = areaService.countByMap(map);
            if (organNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其它分布名重复");
            } else {
                areaService.update(area);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改分布出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改分布出错");
        }
        return configResponse;
    }

    /**
     * @param ids 分布ids
     * @return java.lang.Object 删除的结果
     * @description 根据ids删除分布
     * @author maodi
     * @createDate 2018/6/13 14:36
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            userAreaService.deleteByAreaIds(ids);
            organAreaService.deleteByAreaIds(ids);
            areaService.deleteByIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("删除分布出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "删除分布出错");
        }
    }

}
