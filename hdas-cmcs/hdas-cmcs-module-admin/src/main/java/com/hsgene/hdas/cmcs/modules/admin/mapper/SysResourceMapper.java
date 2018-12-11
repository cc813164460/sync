package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.SysResource;
import org.springframework.stereotype.Repository;

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
public interface SysResourceMapper extends BaseMapper<SysResource> {

    long[] getAllId();

}
