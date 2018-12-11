package com.hsgene.hdas.cmcs.modules.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description: 系统控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/6 15:46
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Controller
public class SystemController {

    /**
     * @param
     * @return java.lang.String  index页面名字
     * @description 将项目根目录定位到index页面
     * @author maodi
     * @createDate 2018/6/13 15:37
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root() {
        return "index";
    }

    /**
     * @param
     * @return java.lang.String   index页面的名字
     * @description 请求重定向到index页面
     * @author maodi
     * @createDate 2018/9/7 16:15
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /**
     * @param
     * @return java.lang.String  timeout页面的名字
     * @description 请求重定向到timeout页面
     * @author maodi
     * @createDate 2018/9/7 16:16
     */
    @RequestMapping(value = "/timeout", method = RequestMethod.GET)
    public String timeout() {
        return "timeout";
    }

    /**
     * @param
     * @return java.lang.String  password_update页面的名字
     * @description 请求重定向到password_update页面
     * @author maodi
     * @createDate 2018/9/7 16:17
     */
    @RequestMapping(value = "/password_update", method = RequestMethod.GET)
    public String passwordUpdate() {
        return "password_update";
    }

    /**
     * @param
     * @return java.lang.String error页面的名字
     * @description 请求重定向到error页面
     * @author maodi
     * @createDate 2018/9/7 16:18
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error() {
        return "error";
    }

}
