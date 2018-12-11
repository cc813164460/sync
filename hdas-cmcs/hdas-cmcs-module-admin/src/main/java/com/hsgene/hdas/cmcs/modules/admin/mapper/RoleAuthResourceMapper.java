package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.RoleAuthResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @description: 角色权限关系mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface RoleAuthResourceMapper extends BaseMapper<RoleAuthResource> {

    void deleteByRoleIds(long[] roleIds);

    void deleteByAuthIds(long[] authIds);

    void deleteByProductIds(long[] productIds);

    void deleteByModuleIds(long[] moduleIds);

    void deleteByEnvIds(long[] envIds);

    void deleteByVersionIds(long[] versionIds);

    long[] getArIdsByRoleIds(long[] roleIds);

    List<Map<String, Object>> getUserAuthResource(Map<String, Object> map);

    List<Map<String, Object>> getSelectByCondition(Map<String, Object> map);

}
