package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.ItemResource;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.ItemResourceMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IItemResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
public class ItemResourceServiceImpl extends BaseServiceImpl<ItemResource> implements IItemResourceService {

    @Autowired
    ItemResourceMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<ItemResource> getBaseMapper() {
        return mapper;
    }

    @Override
    public void overByItemIds(long[] itemIds) {
        mapper.overByItemIds(itemIds);
    }

    @Override
    public void setUseByItemIds(long[] itemIds) {
        mapper.setUseByItemIds(itemIds);
    }

    @Override
    public void deleteByItemIds(long[] itemIds) {
        mapper.deleteByItemIds(itemIds);
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
        mapper.deleteByProductIds(envIds);
    }

    @Override
    public void deleteByVersionIds(long[] versionIds) {
        mapper.deleteByProductIds(versionIds);
    }

    @Override
    public List<Map<String, Object>> getProductModuleEnvList(Map<String, Object> map) {
        return mapper.getProductModuleEnvList(map);
    }

    @Override
    public long[] getResourceIdsByItemId(long itemId) {
        return mapper.getResourceIdsByItemId(itemId);
    }

}
