package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @description: 人员信息实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/13 11:55
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id = -1;
    private String nickname;
    private long areaId;
    private long organId;
    private long[] roleIds;
    private String mobile;
    private String email;
    private String username;
    private String password;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public long getOrganId() {
        return organId;
    }

    public void setOrganId(long organId) {
        this.organId = organId;
    }

    public long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(long[] roleIds) {
        this.roleIds = roleIds;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
               "id=" + id +
               ", nickname='" + nickname + '\'' +
               ", areaId=" + areaId +
               ", organId=" + organId +
               ", roleIds=" + Arrays.toString(roleIds) +
               ", mobile='" + mobile + '\'' +
               ", email='" + email + '\'' +
               ", username='" + username + '\'' +
               ", password='" + password + '\'' +
               '}';
    }

}
