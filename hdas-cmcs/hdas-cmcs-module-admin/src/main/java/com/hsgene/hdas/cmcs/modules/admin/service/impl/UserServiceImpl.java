package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.SysPermission;
import com.hsgene.hdas.cmcs.modules.admin.domain.User;
import com.hsgene.hdas.cmcs.modules.admin.dto.AuthGrantedAuthority;
import com.hsgene.hdas.cmcs.modules.admin.dto.CustomUserDetails;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.SysPermissionMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.UserMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserService;
import com.hsgene.hdas.cmcs.modules.common.util.EncrypAESUtil;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 人员实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements IUserService, UserDetailsService {

    @Autowired
    UserMapper mapper;

    @Autowired
    SysPermissionMapper sysPermissionMapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<User> getBaseMapper() {
        return mapper;
    }

    /**
     * @param username 账号名
     * @return com.hsgene.hdas.cmcs.modules.admin.domain.User  用户
     * @description 根据账号名获取用户
     * @author maodi
     * @createDate 2018/6/13 16:07
     */
    @Override
    public User getUserByUsername(String username) {
        return mapper.getUserByUsername(username);
    }

    /**
     * @param username 账号名
     * @return void
     * @description 根据账号名修改用户最近登录时间
     * @author maodi
     * @createDate 2018/6/13 16:07
     */
    @Override
    public void updateLastDateTime(String username, String lastDateTime) {
        mapper.updateLastDateTime(username, lastDateTime);
    }

    /**
     * @param username 账号名
     * @return java.util.Map<java.lang.String,java.lang.Object> 人员登录信息
     * @description 根据账号名获取人员登录信息
     * @author maodi
     * @createDate 2018/6/13 16:08
     */
    @Override
    public Map<String, Object> getLoginInfoByUsername(String username) {
        return mapper.getLoginInfoByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //更新用户登录时间
        mapper.updateLastDateTime(username, StringUtil.getFormatTime());
        User user = mapper.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        //GrantedAuthority是security提供的权限类，
        List<GrantedAuthority> list = new ArrayList<>();
        getAuthorities(user, list);
        user.setPassword(EncrypAESUtil.decryptTostring(user.getPassword()));
        //返回包括权限角色的User给security
        CustomUserDetails userDetails = new CustomUserDetails(user, true, true, true, true, list);
        return userDetails;
    }

    public void getAuthorities(User user, List<GrantedAuthority> list) {
        List<SysPermission> sysPermissions = sysPermissionMapper.getByUserId(user.getId());
        for (SysPermission sysPermission : sysPermissions) {
            String url = sysPermission.getUrl();
            String httpMethod = sysPermission.getMethod();
            list.add(new AuthGrantedAuthority(url, httpMethod));
        }
    }

    @Override
    public int countMobileByMap(Map<String, Object> map) {
        return mapper.countMobileByMap(map);
    }

    @Override
    public int countEmailByMap(Map<String, Object> map) {
        return mapper.countEmailByMap(map);
    }

    @Override
    public User getByUsername(String username) {
        return mapper.getByUsername(username);
    }

}
