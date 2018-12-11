package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
public interface ResourceMapper extends BaseMapper<Resource> {

    void deleteByProductIds(long[] userIds);

    void deleteByModuleIdAndNotInProductIds(@Param(value = "moduleId") long moduleId, @Param(value = "productIds")
            long[] productIds);

    void deleteByModuleIds(long[] moduleIds);

    void deleteByEnvIds(long[] envIds);

    void deleteByVersionIds(long[] versionIds);

    List<Map<String, Object>> getResource();

    long getIdByProductModuleEnvVersionId(Map<String, Object> map);

}
