package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.App;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IAppService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: app控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/app")
public class AppController {

    @Autowired
    private IAppService appService;

    /**
     * @param itemId 条目id
     * @return java.lang.Object app数据，
     * @description 根据itemId获取app数据，为app列表提供数据
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping("/app_data")
    public @ResponseBody
    Object getSelectByMap(@RequestParam(value = "itemId", required = false, defaultValue = "-1") long itemId) {
        try {
            if (itemId == -1) {
                return appService.getAll();
            } else {
                Map<String, Object> map = new HashMap<>(16);
                map.put("itemId", itemId);
                return appService.getSelectByMap(map);
            }
        } catch (Exception e) {
            log.error("根据itemId获取app数据，为app列表提供数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据itemId获取app数据，为app列表提供数据出错");
        }
    }

    /**
     * @param app app实体
     * @return java.lang.Object 新增结果
     * @description 新增app
     * @author maodi
     * @createDate 2018/8/23 14:59
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    Object insert(App app) {
        ConfigResponse configResponse;
        try {
            String ip = app.getIp();
            Map<String, Object> map = new HashMap<>(16);
            map.put("ip", ip);
            int organNum = appService.countByMap(map);
            if (organNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "新增失败！跟其它ip地址重复");
            } else {
                if (app.getId() == -1) {
                    app.setId(StringUtil.getId());
                }
                appService.save(app);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "新增成功！");
            }
        } catch (Exception e) {
            log.error("新增app出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增app出错");
        }
        return configResponse;
    }

    /**
     * @param app app实体
     * @return java.lang.Object 修改app结果
     * @description 修改app
     * @author maodi
     * @createDate 2018/8/23 15:00
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(App app) {
        ConfigResponse configResponse;
        try {
            String ip = app.getIp();
            long id = app.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("ip", ip);
            map.put("id", id);
            int organNum = appService.countByMap(map);
            if (organNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其它ip地址重复");
            } else {
                appService.update(app);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改app出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改app出错");
        }
        return configResponse;
    }

}
