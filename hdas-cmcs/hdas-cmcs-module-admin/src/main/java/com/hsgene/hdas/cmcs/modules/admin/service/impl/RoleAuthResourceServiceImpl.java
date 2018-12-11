package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.Constant;
import com.hsgene.hdas.cmcs.modules.admin.domain.RoleAuthResource;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.RoleAuthResourceMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IRoleAuthResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @description: 人员角色关系实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class RoleAuthResourceServiceImpl extends BaseServiceImpl<RoleAuthResource> implements IRoleAuthResourceService {

    @Autowired
    RoleAuthResourceMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<RoleAuthResource> getBaseMapper() {
        return mapper;
    }

    @Override
    public List<Map<String, Object>> getSelectByMap(Map<String, Object> map) {
        List<Map<String, Object>> listMap = mapper.getSelectByMap(map);
        dealAuthSelect(listMap);
        return listMap;
    }

    @Override
    public void deleteByRoleIds(long[] roleIds) {
        mapper.deleteByRoleIds(roleIds);
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
    public long[] getArIdsByRoleIds(long[] roleIds) {
        return mapper.getArIdsByRoleIds(roleIds);
    }

    @Override
    public List<Map<String, Object>> getUserAuthResource(Map<String, Object> map) {
        return mapper.getUserAuthResource(map);
    }

    @Override
    public List<Map<String, Object>> getSelectByCondition(Map<String, Object> map) {
        List<Map<String, Object>> listMap = mapper.getSelectByCondition(map);
        dealAuthSelect(listMap);
        return listMap;
    }

    public void dealAuthSelect(List<Map<String, Object>> listMap) {
        for (Map<String, Object> itemMap : listMap) {
            String[] auths = itemMap.get("auths").toString().split(",");
            itemMap.put(Constant.SELECT_EN, 0);
            itemMap.put(Constant.INSERT_EN, 0);
            itemMap.put(Constant.UPDATE_EN, 0);
            itemMap.put(Constant.DELETE_EN, 0);
            itemMap.put(Constant.RELEASE_EN, 0);
            itemMap.put(Constant.OFFLINE_EN, 0);
            for (String auth : auths) {
                if (Constant.SELECT_CN.equals(auth)) {
                    itemMap.put(Constant.SELECT_EN, 1);
                } else if (Constant.INSERT_CN.equals(auth)) {
                    itemMap.put(Constant.INSERT_EN, 1);
                } else if (Constant.UPDATE_CN.equals(auth)) {
                    itemMap.put(Constant.UPDATE_EN, 1);
                } else if (Constant.DELETE_CN.equals(auth)) {
                    itemMap.put(Constant.DELETE_EN, 1);
                } else if (Constant.RELEASE_CN.equals(auth)) {
                    itemMap.put(Constant.RELEASE_EN, 1);
                } else if (Constant.OFFLINE_CN.equals(auth)) {
                    itemMap.put(Constant.OFFLINE_EN, 1);
                }
            }
        }
    }

}
