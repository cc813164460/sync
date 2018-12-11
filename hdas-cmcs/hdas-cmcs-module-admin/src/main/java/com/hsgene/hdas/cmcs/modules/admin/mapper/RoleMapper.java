package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.Role;
import org.springframework.stereotype.Repository;

/**
 * @description: 角色mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface RoleMapper extends BaseMapper<Role> {

    String[] getRoleNamesByIds(long[] ids);

    String[] getDescriptionById(long[] ids);

}
