package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.Env;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.EnvMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IEnvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: 环境实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/12 9:10
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class EnvServiceImpl extends BaseServiceImpl<Env> implements IEnvService {

    @Autowired
    EnvMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<Env> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteNotInIds(long[] ids) {
        mapper.deleteNotInIds(ids);
    }

}
