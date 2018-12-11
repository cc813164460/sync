package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.AuthResource;
import org.springframework.stereotype.Repository;

/**
 * @description: 权限资源mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/5/28 17:01
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface AuthResourceMapper extends BaseMapper<AuthResource> {

    void deleteByAuthIds(long[] authIds);

    void deleteByProductIds(long[] productIds);

    void deleteByModuleIds(long[] moduleIds);

    void deleteByEnvIds(long[] envIds);

    void deleteByVersionIds(long[] versionIds);

    void deleteByResourceIds(long[] resourceIds);

}
