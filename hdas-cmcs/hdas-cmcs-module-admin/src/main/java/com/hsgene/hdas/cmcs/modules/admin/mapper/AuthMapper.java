package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.Auth;
import org.springframework.stereotype.Repository;

/**
 * @description: 权限mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/5/28 17:01
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface AuthMapper extends BaseMapper<Auth> {

    long[] getAllId();

}
