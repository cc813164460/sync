package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.ModuleProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @description: 环境项目mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/5/30 10:34
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface ModuleProductMapper extends BaseMapper<ModuleProduct> {

    void deleteByModuleIds(long[] moduleIds);

    void deleteByProductIds(long[] productIds);

    long[] getDistinctModuleIds();

    List<Map<String, Object>> getProductModuleData(Map<String, Object> map);
}
