package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.InstanceType;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.InstanceTypeMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IInstanceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class InstanceTypeServiceImpl extends BaseServiceImpl<InstanceType> implements IInstanceTypeService {

    @Autowired
    InstanceTypeMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<InstanceType> getBaseMapper() {
        return mapper;
    }

}
