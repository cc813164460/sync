package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.Resource;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.ResourceMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IResourceService;
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
public class ResouceServiceImpl extends BaseServiceImpl<Resource> implements IResourceService {

    @Autowired
    ResourceMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<Resource> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByProductIds(long[] productIds) {
        mapper.deleteByProductIds(productIds);
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
    public void deleteByEnvIds(long[] envIds) {
        mapper.deleteByEnvIds(envIds);
    }

    @Override
    public void deleteByVersionIds(long[] versionIds) {
        mapper.deleteByVersionIds(versionIds);
    }

    @Override
    public List<Map<String, Object>> getResource() {
        return mapper.getResource();
    }

    @Override
    public long getIdByProductModuleEnvVersionId(Map<String, Object> map) {
        return mapper.getIdByProductModuleEnvVersionId(map);
    }

}
