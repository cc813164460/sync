package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 条目控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    /**
     * @param id itemId
     * @return java.lang.Object 安全级别id
     * @description 根据itemId获取安全级别
     * @author maodi
     * @createDate 2018/8/31 14:28
     */
    @RequestMapping("/get_classId_by_id")
    public @ResponseBody
    Object getClassIdById(@RequestParam(value = "id", required = false, defaultValue = "-1") long id) {
        try {
            long classId = itemService.getClassIdById(id);
            return new ConfigResponse(HttpStatus.OK.value(), classId, "根据id获取classId成功");
        } catch (Exception e) {
            log.error("根据id获取classId出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), id, "根据id获取classId出错");
        }
    }

}
