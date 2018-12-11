package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.OrganArea;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.OrganAreaMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IOrganAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 部门分布关系实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/6 10:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class OrganAreaServiceImpl extends BaseServiceImpl<OrganArea> implements IOrganAreaService {

    @Autowired
    OrganAreaMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<OrganArea> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByOrganIds(long[] ids) {
        mapper.deleteByOrganIds(ids);
    }

    @Override
    public void deleteByAreaIds(long[] areaIds) {
        mapper.deleteByAreaIds(areaIds);
    }

}
