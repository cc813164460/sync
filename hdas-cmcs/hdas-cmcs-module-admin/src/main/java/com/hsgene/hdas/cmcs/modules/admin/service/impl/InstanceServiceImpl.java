package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hsgene.hdas.cmcs.modules.admin.domain.Instance;
import com.hsgene.hdas.cmcs.modules.admin.mapper.*;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;
import com.hsgene.hdas.cmcs.modules.admin.service.IInstanceService;
import com.hsgene.hdas.cmcs.modules.admin.util.PageInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 版本实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class InstanceServiceImpl extends BaseServiceImpl<Instance> implements IInstanceService {

    @Autowired
    InstanceMapper mapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RoleClassMapper roleClassMapper;

    @Autowired
    ResourceMapper resourceMapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Area>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<Instance> getBaseMapper() {
        return mapper;
    }

    @Override
    public PageInfo<Map<String, Object>> selectPropertiesByPage(HttpServletRequest hsr, long resourceId, long
            instanceId, long maxClassId) {
        return dealSelectPropertiesOfPage(hsr, resourceId, null, instanceId, maxClassId);
    }

    @Override
    public PageInfo<Map<String, Object>> selectPropertiesByKeyPage(HttpServletRequest hsr, long resourceId, String
            key, long instanceId, long maxClassId) {
        return dealSelectPropertiesOfPage(hsr, resourceId, key, instanceId, maxClassId);
    }

    @Override
    public long getIdByResourceIdAndInstanceTypeId(Map<String, Object> map) {
        return mapper.getIdByResourceIdAndInstanceTypeId(map) == null ? -1 : (long) mapper
                .getIdByResourceIdAndInstanceTypeId(map);
    }

    @Override
    public long getResourceIdByIds(long[] ids) {
        return mapper.getResourceIdByIds(ids);
    }

    @Override
    public List<Map<String, Object>> getAllKeyAndValue(HttpServletRequest hsr, String username) {
        long userId = userMapper.getIdByName(username);
        long maxClassId = roleClassMapper.getMaxClassIdByUserId(userId);
        if ("admin".equals(username)) {
            maxClassId = 2;
        }
        String productModuleEnvId = hsr.getParameter("productModuleEnvId");
        long versionId = Long.valueOf(hsr.getParameter("versionId"));
        String[] ids = productModuleEnvId.split("_");
        long productId = Long.valueOf(ids[0]);
        long moduleId = Long.valueOf(ids[1]);
        long envId = Long.valueOf(ids[2]);
        Map<String, Object> map = new HashMap<>(16);
        map.put("productId", productId);
        map.put("moduleId", moduleId);
        map.put("envId", envId);
        map.put("versionId", versionId);
        long resourceId = resourceMapper.getIdByProductModuleEnvVersionId(map);
        return dealDataByClassId(resourceId, maxClassId);
    }

    @Override
    public long[] getInstanceIdsByResourceIds(long[] resourceIds) {
        return mapper.getInstanceIdsByResourceIds(resourceIds);
    }

    public List<Map<String, Object>> dealDataByClassId(long resourceId, long maxClassId) {
        List<Map<String, Object>> listMap = mapper.getAllKeyAndValue(resourceId);
        for (Map<String, Object> tempMap : listMap) {
            long classId = (long) tempMap.get("class_id");
            if (maxClassId < classId) {
                tempMap.put("value", "******");
            }
        }
        return listMap;
    }

    public PageInfo<Map<String, Object>> dealSelectPropertiesOfPage(HttpServletRequest hsr, long resourceId, String
            key, long instanceId, long maxClassId) {
        int draw = Integer.valueOf(hsr.getParameter("draw") == null ? "1" : hsr.getParameter("draw"));
        int start = Integer.valueOf(hsr.getParameter("start") == null ? "0" : hsr.getParameter("start"));
        int pageSize = Integer.valueOf(hsr.getParameter("length") == null ? "10" : hsr.getParameter("length"));
        int pageNum = 1;
        if (pageSize < 1) {
            pageSize = 0;
        }
        if (pageSize != 0) {
            pageNum = (start / pageSize) + 1;
        }
        PageHelper.startPage(pageNum, pageSize);
        // 需要把Page包装成PageInfo对象才能序列化。该插件也默认实现了一个PageInfo
        Page<Map<String, Object>> page;
        if (key == null) {
            page = mapper.selectPropertiesByPage(resourceId, instanceId);
        } else {
            Map<String, Object> map = new HashMap<>(16);
            map.put("resourceId", resourceId);
            map.put("key", key);
            map.put("instanceId", instanceId);
            page = mapper.selectPropertiesByKeyPage(map);
        }
        for (Map<String, Object> map : page) {
            long classId = (long) map.get("class_id");
            if (maxClassId < classId) {
                map.put("value", "******");
                map.put("hasRole", "0");
            } else {
                map.put("hasRole", "1");
            }
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(page);
        pageInfo.setDraw(draw);
        pageInfo.setPageNum(pageNum);
        PageInfoUtil.addNumToList(pageInfo);
        return pageInfo;
    }

}
