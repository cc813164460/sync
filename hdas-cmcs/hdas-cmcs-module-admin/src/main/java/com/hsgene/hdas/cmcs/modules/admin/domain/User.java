package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description: 人员实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/5/28 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class User implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private String username;
    private String nickname;
    private String mobile;
    private String email;
    private Timestamp createDateTime;
    private int isDelete;
    private String password;
    private Timestamp lastDateTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public Timestamp getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Timestamp createDateTime) {
        this.createDateTime = createDateTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getLastDateTime() {
        return lastDateTime;
    }

    public void setLastDateTime(Timestamp lastDateTime) {
        this.lastDateTime = lastDateTime;
    }

    @Override
    public String toString() {
        return this.username;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.toString().equals(obj.toString());
    }

}
