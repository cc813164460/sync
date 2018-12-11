package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.SysPermission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 资源mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/5/28 17:01
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    void deleteByRoleIds(long[] roleIds);

    List<SysPermission> getByUserId(@Param("userId") long userId);

}
