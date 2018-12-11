package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IOrganService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 部门控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/12 9:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/organ")
public class OrganController {

    @Autowired
    private IOrganService organService;

    /**
     * @param areaId 分布id
     * @return java.lang.Object  部门数据
     * @description 根据分布id获取部门数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 14:54
     */
    @RequestMapping("/organ_data")
    public @ResponseBody
    Object getSelectByMap(@RequestParam(value = "areaId", required = false, defaultValue = "-1") long areaId) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            if (areaId != -1) {
                map.put("areaId", areaId);
            }
            return organService.getSelectByMap(map);
        } catch (Exception e) {
            log.error("根据分布id获取部门数据，为下拉框提供数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据分布id获取部门数据，为下拉框提供数据出错");
        }
    }

}
