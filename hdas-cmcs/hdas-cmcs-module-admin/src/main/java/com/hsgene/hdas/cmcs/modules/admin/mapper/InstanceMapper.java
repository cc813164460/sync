package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.github.pagehelper.Page;
import com.hsgene.hdas.cmcs.modules.admin.domain.Instance;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @description: 分布mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface InstanceMapper extends BaseMapper<Instance> {

    Page<Map<String, Object>> selectPropertiesByPage(@Param("resourceId") long resourceId, @Param("instanceId")
            long instanceId);

    Page<Map<String, Object>> selectPropertiesByKeyPage(Map<String, Object> map);

    List<Map<String, Object>> getAllKeyAndValue(@Param("resourceId") long resourceId);

    Object getIdByResourceIdAndInstanceTypeId(Map<String, Object> map);

    Instance getByResourceId(long resourceId);

    long getResourceIdByIds(long[] ids);

    long[] getInstanceIdsByResourceIds(long[] resourceIds);

}
