package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.ModuleProduct;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.ModuleProductMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IModuleProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 环境项目关系实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/6 10:23
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class ModuleProductServiceImpl extends BaseServiceImpl<ModuleProduct> implements IModuleProductService {

    @Autowired
    ModuleProductMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<ModuleProduct> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByModuleIds(long[] envIds) {
        mapper.deleteByModuleIds(envIds);
    }

    @Override
    public void deleteByProductIds(long[] productIds) {
        mapper.deleteByProductIds(productIds);
    }

    @Override
    public long[] getDistinctModuleIds() {
        return mapper.getDistinctModuleIds();
    }

    @Override
    public List<Map<String, Object>> getProductModuleData(Map<String, Object> map) {
        return mapper.getProductModuleData(map);
    }
}
