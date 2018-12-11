package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.Resource;

import java.util.List;
import java.util.Map;

/**
 * @description: 分布接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IResourceService extends IBaseService<Resource> {

    void deleteByProductIds(long[] productIds);

    void deleteByModuleIdAndNotInProductIds(long moduleId, long[] productIds);

    void deleteByModuleIds(long[] moduleIds);

    void deleteByEnvIds(long[] envIds);

    void deleteByVersionIds(long[] versionIds);

    List<Map<String, Object>> getResource();

    long getIdByProductModuleEnvVersionId(Map<String, Object> map);

}
