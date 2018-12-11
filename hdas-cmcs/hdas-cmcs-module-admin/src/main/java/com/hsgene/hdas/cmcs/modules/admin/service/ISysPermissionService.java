package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.SysPermission;

import java.util.List;

/**
 * @description: 分布接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface ISysPermissionService extends IBaseService<SysPermission> {

    void deleteByRoleIds(long[] roleIds);

    List<SysPermission> getByUserId(long userId);

}
