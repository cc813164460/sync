package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @description: 人员mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/5/28 17:01
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

    /**
     * @param username 账号名
     * @return com.hsgene.hdas.cmcs.modules.admin.domain.User  用户
     * @description 根据账号名获取用户
     * @author maodi
     * @createDate 2018/6/13 16:07
     */
    User getUserByUsername(@Param("username") String username);

    /**
     * @param username 账号名
     * @return void
     * @description 根据账号名修改用户最近登录时间
     * @author maodi
     * @createDate 2018/6/13 16:07
     */
    void updateLastDateTime(@Param("username") String username, @Param("lastDateTime") String lastDateTime);

    /**
     * @param username 账号名
     * @return java.util.Map<java.lang.String,java.lang.Object> 人员登录信息
     * @description 根据账号名获取人员登录信息
     * @author maodi
     * @createDate 2018/6/13 16:08
     */
    Map<String, Object> getLoginInfoByUsername(@Param("username") String username);

    int countMobileByMap(Map<String, Object> map);

    int countEmailByMap(Map<String, Object> map);

    User getByUsername(@Param("username") String username);

}
