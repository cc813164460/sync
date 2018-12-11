package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.EnvModuleProduct;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.EnvModuleProductMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IEnvModuleProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 环境模块项目关系实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/6 10:23
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class EnvModuleProductServiceImpl extends BaseServiceImpl<EnvModuleProduct> implements IEnvModuleProductService {

    @Autowired
    EnvModuleProductMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<EnvModuleProduct> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByEnvIds(long[] envIds) {
        mapper.deleteByEnvIds(envIds);
    }

    @Override
    public void deleteByModuleIdAndNotInProductIds(long moduleId, long[] productIds) {
        mapper.deleteByModuleIdAndNotInProductIds(moduleId, productIds);
    }

    @Override
    public void deleteByModuleIds(long[] moduleIds) {
        mapper.deleteByModuleIds(moduleIds);
    }

    @Override
    public void deleteByProductIds(long[] productIds) {
        mapper.deleteByProductIds(productIds);
    }

    @Override
    public long[] getDistinctEnvIds() {
        return mapper.getDistinctEnvIds();
    }

}
