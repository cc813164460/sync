package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IModuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 模块控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/12 9:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/module")
public class ModuleController {

    @Autowired
    private IModuleService moduleService;

    /**
     * @param
     * @return java.lang.Object 模块数据，id、name的键值对组成的链表
     * @description 获取模块数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 14:42
     */
    @RequestMapping("/module_data")
    public @ResponseBody
    Object getSelectByMap() {
        try {
            return moduleService.getSelectByMap();
        } catch (Exception e) {
            log.error("获取模块下拉框数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取模块下拉框数据出错");
        }
    }

}
