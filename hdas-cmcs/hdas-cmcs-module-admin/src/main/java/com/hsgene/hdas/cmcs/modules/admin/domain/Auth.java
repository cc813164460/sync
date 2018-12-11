package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: 权限实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:16
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Auth implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private int isDelete;
    private int selectAuth;
    private int insertAuth;
    private int updateAuth;
    private int deleteAuth;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSelectAuth() {
        return selectAuth;
    }

    public void setSelectAuth(int selectAuth) {
        this.selectAuth = selectAuth;
    }

    public int getInsertAuth() {
        return insertAuth;
    }

    public void setInsertAuth(int insertAuth) {
        this.insertAuth = insertAuth;
    }

    public int getUpdateAuth() {
        return updateAuth;
    }

    public void setUpdateAuth(int updateAuth) {
        this.updateAuth = updateAuth;
    }

    public int getDeleteAuth() {
        return deleteAuth;
    }

    public void setDeleteAuth(int deleteAuth) {
        this.deleteAuth = deleteAuth;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Auth{" +
               "id=" + id +
               ", isDelete=" + isDelete +
               ", selectAuth=" + selectAuth +
               ", insertAuth=" + insertAuth +
               ", updateAuth=" + updateAuth +
               ", deleteAuth=" + deleteAuth +
               '}';
    }

}
