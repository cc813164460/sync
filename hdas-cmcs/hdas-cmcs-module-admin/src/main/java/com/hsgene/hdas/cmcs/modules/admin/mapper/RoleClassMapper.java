package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.RoleClass;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @description: 人员角色关系mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface RoleClassMapper extends BaseMapper<RoleClass> {

    long getClassIdByRoleId(@Param(value = "roleId")long roleId);

    long getIdByRoleId(@Param(value = "roleId") long roleId);

    void deleteByRoleIds(long[] roleIds);

    long getMaxClassIdByUserId(@Param(value = "userId") long userId);

}
