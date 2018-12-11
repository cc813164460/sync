package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.RoleClass;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.RoleClassMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IRoleClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 人员角色关系实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class RoleClassServiceImpl extends BaseServiceImpl<RoleClass> implements IRoleClassService {

    @Autowired
    RoleClassMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<RoleClass> getBaseMapper() {
        return mapper;
    }


    @Override
    public long getClassIdByRoleId(long roleId) {
        return mapper.getClassIdByRoleId(roleId);
    }

    @Override
    public long getIdByRoleId(long roleId) {
        return mapper.getIdByRoleId(roleId);
    }

    @Override
    public void deleteByRoleIds(long[] roleIds) {
        mapper.deleteByRoleIds(roleIds);
    }

    @Override
    public long getMaxClassIdByUserId(long userId) {
        return mapper.getMaxClassIdByUserId(userId);
    }

}
