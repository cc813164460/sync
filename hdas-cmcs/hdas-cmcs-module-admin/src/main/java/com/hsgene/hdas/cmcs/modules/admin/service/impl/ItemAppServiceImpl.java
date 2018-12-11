package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.ItemApp;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.ItemAppMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IItemAppService;
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
public class ItemAppServiceImpl extends BaseServiceImpl<ItemApp> implements IItemAppService {

    @Autowired
    ItemAppMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<ItemApp> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByAppIds(long[] appIds) {
        mapper.deleteByAppIds(appIds);
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
    public long[] getItemIdsByAppId(Map<String, Object> map) {
        return mapper.getItemIdsByAppId(map);
    }

    @Override
    public List<Map<String, Object>> getItemIdAndAppIdsByItemIds(long[] itemIds) {
        return mapper.getItemIdAndAppIdsByItemIds(itemIds);
    }

    @Override
    public long[] getAppIdsByItemId(long itemId) {
        return mapper.getAppIdsByItemId(itemId);
    }

}
