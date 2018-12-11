package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.RoleAuthResource;

import java.util.List;
import java.util.Map;

/**
 * @description: 角色权限关系接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IRoleAuthResourceService extends IBaseService<RoleAuthResource> {

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
