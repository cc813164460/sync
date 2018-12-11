package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IEnvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 环境控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/12 9:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/env")
public class EnvController {

    @Autowired
    private IEnvService envService;

    /**
     * @param
     * @return java.lang.Object 环境数据，id、name的键值对组成的链表
     * @description 获取环境数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 14:42
     */
    @RequestMapping("/env_data")
    public @ResponseBody
    Object getSelectByMap() {
        try {
            return envService.getSelectByMap();
        } catch (Exception e) {
            log.error("获取环境下拉框数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取环境下拉框数据出错");
        }
    }

}
