package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.*;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.*;
import com.hsgene.hdas.cmcs.modules.common.util.EncrypAESUtil;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 人员控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserAreaService userAreaService;

    @Autowired
    private IUserOrganService userOrganService;

    @Autowired
    private IUserRoleService userRoleService;

    @Autowired
    private IRoleClassService roleClassService;

    /**
     * @param
     * @return java.lang.String  人员新增页名字
     * @description 获取人员新增页
     * @author maodi
     * @createDate 2018/6/13 15:38
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/user_add";
    }

    /**
     * @param
     * @return java.lang.String 人员修改页名字
     * @description 获取人员修改页
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/user_update";
    }


    /**
     * @param areaId  分布id
     * @param organId 部门id
     * @return java.lang.Object 人员数据，id、name的键值对组成的链表
     * @description 根据areaId、organId获取人员数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 15:39
     */
    @RequestMapping("/user_data")
    public @ResponseBody
    Object getSelectByMap(@RequestParam(value = "areaId", required = false, defaultValue = "-1") long areaId,
                          @RequestParam(value = "organId", required = false, defaultValue = "-1") long organId) {
        try {
            Map<String, Object> map = new HashMap<>(16);
            if (areaId != -1) {
                map.put("areaId", areaId);
            }
            if (organId != -1) {
                map.put("organId", organId);
            }
            return userService.getSelectByMap(map);
        } catch (Exception e) {
            log.error("根据areaId、organId获取人员数据，为下拉框提供数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据areaId、organId获取人员数据，为下拉框提供数据出错");
        }
    }

    /**
     * @param userInfo 人员信息
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse   新增结果
     * @description 新增人员
     * @author maodi
     * @createDate 2018/6/13 15:41
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(UserInfo userInfo) {
        ConfigResponse configResponse;
        try {
            String name = userInfo.getUsername();
            String mobile = userInfo.getMobile();
            String email = userInfo.getEmail();
            Map<String, Object> map = new HashMap<>(16);
            map.put("username", name);
            map.put("mobile", mobile);
            map.put("email", email);
            int userNum = userService.countByMap(map);
            int mobileNum = userService.countMobileByMap(map);
            int emailNum = userService.countEmailByMap(map);
            if (userNum > 0 || mobileNum > 0 || emailNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, getDuplicateMessage(userNum,
                        mobileNum, emailNum));
            } else {
                action(userInfo);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("添加人员出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "添加人员出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 人员分页数据
     * @description 根据名字获取人员分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByNamePage(HttpServletRequest hsr) {
        try {
            PageInfo<Map<String, Object>> datas = userService.selectByNamePage(hsr);
            return decryptPassword(datas);
        } catch (Exception e) {
            log.error("根据名字获取人员分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据名字获取人员分页数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 人员分页数据
     * @description 获取人员分页数据
     * @author maodi
     * @createDate 2018/6/13 15:41
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            PageInfo<Map<String, Object>> datas = userService.selectByPage(hsr);
            return decryptPassword(datas);
        } catch (NumberFormatException e) {
            log.error("获取人员分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取人员分页数据出错");
        }
    }

    /**
     * @param userInfo 人员信息
     * @return java.lang.Object  修改结果
     * @description 修改人员
     * @author maodi
     * @createDate 2018/6/13 15:42
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(UserInfo userInfo) {
        ConfigResponse configResponse;
        try {

            String name = userInfo.getUsername();
            String mobile = userInfo.getMobile();
            String email = userInfo.getEmail();
            long id = userInfo.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("username", name);
            map.put("mobile", mobile);
            map.put("email", email);
            map.put("id", id);
            int userNum = userService.countByMap(map);
            int mobileNum = userService.countMobileByMap(map);
            int emailNum = userService.countEmailByMap(map);
            if (userNum > 0 || mobileNum > 0 || emailNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, getDuplicateMessage(userNum,
                        mobileNum, emailNum));
            } else {
                long[] userIds = {userInfo.getId()};
                deleteAction(userIds);
                action(userInfo);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改人员出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改人员出错");
        }
        return configResponse;
    }

    /**
     * @param username 用户名
     * @param userInfo 用户信息
     * @return java.lang.Object 修改结果
     * @description 修改密码
     * @author maodi
     * @createDate 2018/9/7 16:18
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update_password")
    public @ResponseBody
    Object updatePassword(@SessionAttribute(Constant.SESSION_KEY) String username, UserInfo userInfo) {
        ConfigResponse configResponse;
        try {
            long id = userService.getIdByName(username);
            User user = new User();
            user.setId(id);
            user.setLastDateTime(StringUtil.getNowTimestamp());
            user.setPassword(EncrypAESUtil.encodeTostring(userInfo.getPassword()));
            userService.update(user);
            configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
        } catch (Exception e) {
            log.error("修改人员出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改人员出错");
        }
        return configResponse;
    }

    /**
     * @param ids 人员ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除人员
     * @author maodi
     * @createDate 2018/6/13 15:42
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            deleteAction(ids);
            userService.deleteByIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除人员出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除人员出错");
        }
    }

    /**
     * @param username 用户名
     * @return java.lang.Object 最高安全级别id
     * @description 根据用户获取最高安全级别id
     * @author maodi
     * @createDate 2018/9/7 16:19
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_max_classId_by_username")
    public @ResponseBody
    Object getMaxClassIdByUsername(@SessionAttribute(Constant.SESSION_KEY) String username) {
        try {
            long userId = userService.getIdByName(username);
            long maxClassId = roleClassService.getMaxClassIdByUserId(userId);
            return new ConfigResponse(HttpStatus.OK.value(), maxClassId, "根据用户名获取最大级别成功！");
        } catch (Exception e) {
            log.error("根据用户名获取最大级别", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据用户名获取最大级别");
        }
    }

    /**
     * @param username 用户名
     * @param password 密码
     * @return java.lang.Object 验证结果
     * @description 根据用户名验证密码是否正确
     * @author maodi
     * @createDate 2018/9/7 16:19
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_password_by_username")
    public @ResponseBody
    Object getPasswordByUsername(@SessionAttribute(Constant.SESSION_KEY) String username, String password) {
        User user = userService.getByUsername(username);
        password = EncrypAESUtil.encodeTostring(password);
        if (user.getPassword().equals(password)) {
            return "1";
        }
        return "0";
    }

    /**
     * @param userInfo 人员信息
     * @return void
     * @description 人员信息新增或删除，根据userId是否为-1
     * @author maodi
     * @createDate 2018/6/13 15:42
     */
    private void action(UserInfo userInfo) {
        long userId = userInfo.getId();
        if (userId == -1) {
            userId = StringUtil.getId();
        }
        UserArea userArea = new UserArea();
        userArea.setId(StringUtil.getId());
        userArea.setUserId(userId);
        userArea.setAreaId(userInfo.getAreaId());
        userAreaService.save(userArea);
        UserOrgan userOrgan = new UserOrgan();
        userOrgan.setId(StringUtil.getId());
        userOrgan.setUserId(userId);
        userOrgan.setOrganId(userInfo.getOrganId());
        userOrganService.save(userOrgan);
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        long[] roleIds = userInfo.getRoleIds();
        int num = 0;
        for (long roleId : roleIds) {
            userRole.setId(StringUtil.getId() + num++);
            userRole.setRoleId(roleId);
            userRoleService.save(userRole);
        }
        User user = new User();
        user.setId(userId);
        user.setIsDelete(0);
        user.setNickname(userInfo.getNickname());
        user.setMobile(userInfo.getMobile());
        user.setEmail(userInfo.getEmail());
        user.setUsername(userInfo.getUsername());
        user.setPassword(EncrypAESUtil.encodeTostring(userInfo.getPassword()));
        user.setLastDateTime(StringUtil.getNowTimestamp());
        if (userInfo.getId() == -1) {
            user.setCreateDateTime(StringUtil.getNowTimestamp());
            userService.save(user);
        } else {
            userService.update(user);
        }
    }

    /**
     * @param userIds 人员ids
     * @return void
     * @description 人员信息关系删除
     * @author maodi
     * @createDate 2018/6/13 15:43
     */
    private void deleteAction(long[] userIds) {
        userAreaService.deleteByUserIds(userIds);
        userOrganService.deleteByUserIds(userIds);
        userRoleService.deleteByUserIds(userIds);
    }

    /**
     * @param pageInfo
     * @return java.lang.Object
     * @description 解密密码
     * @author maodi
     * @createDate 2018/6/14 13:56
     */
    private Object decryptPassword(PageInfo<Map<String, Object>> pageInfo) {
        List<Map<String, Object>> datas = pageInfo.getData();
        for (Map<String, Object> data : datas) {
            data.put("password", EncrypAESUtil.decryptTostring(data.get("password").toString()));
        }
        return pageInfo;
    }

    /**
     * @param userNum   用户重复数量
     * @param mobileNum 电话重复数量
     * @param emailNum  邮箱重复数量
     * @return java.lang.String 重复的消息
     * @description 处理用户电话邮箱重复信息
     * @author maodi
     * @createDate 2018/9/7 16:20
     */
    private String getDuplicateMessage(int userNum, int mobileNum, int emailNum) {
        String message = "";
        if (userNum > 0) {
            message += "用户重复,";
        }
        if (mobileNum > 0) {
            message += "电话重复,";
        }
        if (emailNum > 0) {
            message += "邮箱重复";
        }
        return message;
    }

}
