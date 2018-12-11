package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.UserArea;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.UserAreaMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 人员分布关系实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class UserAreaServiceImpl extends BaseServiceImpl<UserArea> implements IUserAreaService {

    @Autowired
    UserAreaMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<UserArea> getBaseMapper() {
        return mapper;
    }

    /**
     * @param userIds
     * @return void
     * @description 根据人员ids删除人员分布关系
     * @author maodi
     * @createDate 2018/6/13 16:25
     */
    @Override
    public void deleteByUserIds(long[] userIds) {
        mapper.deleteByUserIds(userIds);
    }

    @Override
    public void deleteByAreaIds(long[] areaIds) {
        mapper.deleteByAreaIds(areaIds);
    }

}
