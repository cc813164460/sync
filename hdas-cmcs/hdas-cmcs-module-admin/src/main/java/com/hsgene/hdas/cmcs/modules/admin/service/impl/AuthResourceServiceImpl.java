package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.AuthResource;
import com.hsgene.hdas.cmcs.modules.admin.mapper.AuthResourceMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IAuthResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 权限资源实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class AuthResourceServiceImpl extends BaseServiceImpl<AuthResource> implements IAuthResourceService {

    @Autowired
    AuthResourceMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<AuthResource> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByAuthIds(long[] authIds) {
        mapper.deleteByAuthIds(authIds);
    }

    @Override
    public void deleteByProductIds(long[] productIds) {
        mapper.deleteByProductIds(productIds);
    }

    @Override
    public void deleteByModuleIds(long[] moduleIds) {
        mapper.deleteByModuleIds(moduleIds);
    }

    @Override
    public void deleteByEnvIds(long[] envIds) {
        mapper.deleteByEnvIds(envIds);
    }

    @Override
    public void deleteByVersionIds(long[] versionIds) {
        mapper.deleteByVersionIds(versionIds);
    }

    @Override
    public void deleteByResourceIds(long[] resourceIds) {
        mapper.deleteByResourceIds(resourceIds);
    }

}
