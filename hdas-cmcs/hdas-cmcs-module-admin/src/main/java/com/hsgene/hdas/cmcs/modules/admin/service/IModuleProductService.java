package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.ModuleProduct;

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
public interface IModuleProductService extends IBaseService<ModuleProduct> {

    void deleteByModuleIds(long[] envIds);

    void deleteByProductIds(long[] prodcutIds);

    long[] getDistinctModuleIds();

    List<Map<String, Object>> getProductModuleData(Map<String, Object> map);
}
