package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Constant;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: 历史日志管理控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/history_log_manage")
public class HistoryLogManageController {

    @Autowired
    private IItemService itemService;

    @Autowired
    private IItemAppService itemAppService;

    @Autowired
    private IItemResourceService itemResourceService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IHistoryManageService historyManageService;

    @Autowired
    private IRoleClassService roleClassService;

    /**
     * @param request  http请求
     * @param username 用户名
     * @return java.lang.Object 实例properties分页数据
     * @description 获取实例properties分页数据
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object getQueryProperties(HttpServletRequest request, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            if ("admin".equals(username)) {
                maxClassId = 2;
            }
            return historyManageService.getHistory(request, maxClassId);
        } catch (Exception e) {
            log.error("获取历史日志分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取历史日志分页数据出错");
        }
    }

    /**
     * @param request  http请求，包含参数
     * @param username 用户名
     * @return java.lang.Object 实例properties分页数据
     * @description 根据key获取实例properties分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @RequestMapping("/query_by_condition")
    public @ResponseBody
    Object queryByCondition(HttpServletRequest request, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            if ("admin".equals(username)) {
                maxClassId = 2;
            }
            return historyManageService.getHistoryByCondition(request, maxClassId);
        } catch (Exception e) {
            log.error("根据key获取实例properties分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据key获取实例properties分页数据出错");
        }
    }

    /**
     * @param ids       itemIds
     * @param versionId 版本id
     * @param isPublic  是否为公共
     * @param username  用户名
     * @return java.lang.Object 发布的结果
     * @description 根据itemIds, 版本id，是否公共，以及用户名来发布条目
     * @author maodi
     * @createDate 2018/8/23 16:17
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/release")
    public @ResponseBody
    Object release(long[] ids, long versionId, long isPublic, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            //将改item的历史中当前使用的配置的is_delete状态改为2，根据instanceId,key获取同item的历史记录
            long[] useIds = itemService.getUseIdsByIds(ids);
            if (useIds.length > 0) {
                itemService.overByIds(useIds);
                itemAppService.overByItemIds(useIds);
                itemResourceService.overByItemIds(useIds);
            }
            itemService.releaseFromLogByIds(StringUtil.actionMapFrontVersion(ids, userService.getIdByName(username),
                    StringUtil.getFrontVersion(1, versionId, isPublic)));
            itemAppService.setUseByItemIds(ids);
            itemResourceService.setUseByItemIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "发布成功！");
        } catch (Exception e) {
            log.error("根据ids发布历史配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids发布历史配置出错");
        }
    }

    /**
     * @param ids       itemIds
     * @param versionId 版本id
     * @param isPublic  是否为公共
     * @param username  用户名
     * @return java.lang.Object 下线的结果
     * @description 根据itemIds, 版本id，是否公共，以及用户名来下线条目
     * @author maodi
     * @createDate 2018/8/23 16:19
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/offline")
    public @ResponseBody
    Object offline(long[] ids, long versionId, long isPublic, @SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            //将改item的历史其他配置的is_delete状态改为2，根据instanceId,key获取同item的历史记录
            long[] useIds = itemService.getUseIdsByIds(ids);
            if (useIds.length > 0) {
                itemService.overByIds(useIds);
            }
            itemService.offlineFromLogByIds(StringUtil.actionMapFrontVersion(ids, userService.getIdByName(username),
                    StringUtil.getFrontVersion(0, versionId, isPublic)));
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "下线成功！");
        } catch (Exception e) {
            log.error("根据ids下线历史配置出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids下线历史配置出错");
        }
    }

    /**
     * @param versionNumBack 版本号后部分（去除英文字符部分，全为数字）
     * @return java.lang.Object 获取当前使用的条目
     * @description 获取当前使用的条目
     * @author maodi
     * @createDate 2018/8/23 16:20
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_current_use_item")
    public @ResponseBody
    Object getCurrentUseItem(String versionNumBack) {
        try {
            return itemService.getUseByVersionNumBack(versionNumBack);
        } catch (Exception e) {
            log.error("获取当前使用item出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取当前使用item出错");
        }
    }

}
