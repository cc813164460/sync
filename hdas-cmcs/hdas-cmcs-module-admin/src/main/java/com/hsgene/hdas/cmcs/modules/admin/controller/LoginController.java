package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Constant;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserService;
import com.hsgene.hdas.cmcs.modules.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 登录控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/5/25 16:24
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private IUserService userService;

    /**
     * @param
     * @return java.lang.String  登录页名字
     * @description 获取登录页
     * @author maodi
     * @createDate 2018/6/13 14:43
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    /**
     * @param session 会话
     * @return java.lang.String
     * @description 请求登出
     * @author maodi
     * @createDate 2018/6/13 14:45
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        // 移除session
        try {
            session.removeAttribute(Constant.SESSION_KEY);
        } catch (Exception e) {
            log.error("移除session时出错", e);
            throw e;
        }
        return "login";
    }

    /**
     * @param username 账号名
     * @return java.util.Map<java.lang.String,java.lang.Object>  登录用户的信息
     * @description 获取登录的用户信息
     * @author maodi
     * @createDate 2018/6/13 14:45
     */
    @RequestMapping("/get_login_info")
    public @ResponseBody
    Map<String, Object> getLoginInfo(@SessionAttribute(Constant.SESSION_KEY) String username) {
        Map<String, Object> map = new HashMap<>(16);
        Map<String, Object> loginInfo;
        try {
            loginInfo = userService.getLoginInfoByUsername(username);
        } catch (Exception e) {
            log.error("获取登录信息出错", e);
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
        if (loginInfo == null) {
            map.put("success", false);
            map.put("message", "该用户不存在");
            return map;
        }
        Timestamp timestamp = (Timestamp) loginInfo.get("last_date_time");
        loginInfo.put("last_date_time", DateUtil.timestamp2Date(timestamp.getTime(), "yyyy/MM/dd"));
        map.put("success", true);
        map.put("message", loginInfo);
        return map;
    }

}
