package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.UserRole;

/**
 * @description: 人员角色关系接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IUserRoleService extends IBaseService<UserRole> {

    /**
     * @param userIds 人员ids
     * @return void
     * @description 根据人员ids删除人员分布关系
     * @author maodi
     * @createDate 2018/6/13 17:03
     */
    void deleteByUserIds(long[] userIds);

    void deleteByRoleIds(long[] roleIds);

    long[] getRoleIdsByUserId(long userId);

}
