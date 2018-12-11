package com.hsgene.hdas.cmcs.modules.admin.domain;

import java.io.Serializable;

/**
 * @description: app实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.domain
 * @author: maodi
 * @createDate: 2018/6/11 17:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ItemResource implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    private long id;
    private long itemId;
    private long resourceId;
    private int isDelete = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "ItemApp{" +
               "id=" + id +
               ", resourceId=" + resourceId +
               ", itemId=" + itemId +
               ", isDelete=" + isDelete +
               '}';
    }

}
