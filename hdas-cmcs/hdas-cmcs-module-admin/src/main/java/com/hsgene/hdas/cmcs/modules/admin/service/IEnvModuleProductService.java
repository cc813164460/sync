package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.EnvModuleProduct;

import java.util.List;
import java.util.Map;

/**
 * @description: 环境项目关系接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/6 9:44
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IEnvModuleProductService extends IBaseService<EnvModuleProduct> {

    void deleteByEnvIds(long[] envIds);

    void deleteByModuleIdAndNotInProductIds(long moduleId, long[] productIds);

    void deleteByModuleIds(long[] moduleIds);

    void deleteByProductIds(long[] productIds);

    long[] getDistinctEnvIds();

}
