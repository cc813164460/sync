package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.RoleClass;

/**
 * @description: 人员角色关系接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IRoleClassService extends IBaseService<RoleClass> {

    long getClassIdByRoleId(long roleId);

    long getIdByRoleId(long roleId);

    void deleteByRoleIds(long[] roleIds);

    long getMaxClassIdByUserId(long userId);

}
