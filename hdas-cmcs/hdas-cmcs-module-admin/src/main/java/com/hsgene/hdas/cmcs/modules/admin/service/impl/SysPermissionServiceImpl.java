package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.SysPermission;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.SysPermissionMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 版本实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class SysPermissionServiceImpl extends BaseServiceImpl<SysPermission> implements ISysPermissionService {

    @Autowired
    SysPermissionMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<SysPermission> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByRoleIds(long[] roleIds) {
        mapper.deleteByRoleIds(roleIds);
    }

    @Override
    public List<SysPermission> getByUserId(long userId) {
        return mapper.getByUserId(userId);
    }

}
