package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.Instance;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description: 用户接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IInstanceService extends IBaseService<Instance> {

    PageInfo<Map<String, Object>> selectPropertiesByPage(HttpServletRequest hsr, long resourceId, long instanceId,
                                                         long maxClassId);

    PageInfo<Map<String, Object>> selectPropertiesByKeyPage(HttpServletRequest hsr, long resourceId, String key, long
            instanceId, long maxClassId);

    List<Map<String, Object>> getAllKeyAndValue(HttpServletRequest hsr, String username);

    long getIdByResourceIdAndInstanceTypeId(Map<String, Object> map);

    long getResourceIdByIds(long[] ids);

    long[] getInstanceIdsByResourceIds(long[] resourceIds);

}
