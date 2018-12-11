package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.User;

import java.util.Map;

/**
 * @description: 用户接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IUserService extends IBaseService<User> {

    /**
     * @param username 账号名
     * @return com.hsgene.hdas.cmcs.modules.admin.domain.User  用户
     * @description 根据账号名获取用户
     * @author maodi
     * @createDate 2018/6/13 16:07
     */
    User getUserByUsername(String username);

    /**
     * @param username 账号名
     * @return void
     * @description 根据账号名修改用户最近登录时间
     * @author maodi
     * @createDate 2018/6/13 16:07
     */
    void updateLastDateTime(String username, String lastDateTime);

    /**
     * @param username 账号名
     * @return java.util.Map<java.lang.String,java.lang.Object> 人员登录信息
     * @description 根据账号名获取人员登录信息
     * @author maodi
     * @createDate 2018/6/13 16:08
     */
    Map<String, Object> getLoginInfoByUsername(String username);

    int countMobileByMap(Map<String, Object> map);

    int countEmailByMap(Map<String, Object> map);

    User getByUsername(String username);

}
