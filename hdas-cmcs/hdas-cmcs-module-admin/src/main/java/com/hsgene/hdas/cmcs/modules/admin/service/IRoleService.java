package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.Role;

/**
 * @description: 角色接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:03
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IRoleService extends IBaseService<Role> {

    String[] getRoleNamesByIds(long[] ids);

    String[] getDescriptionById(long[] ids);

}
