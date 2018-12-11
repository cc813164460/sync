package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hsgene.hdas.cmcs.modules.admin.domain.Instance;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.PublicConfigMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.RoleClassMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.UserMapper;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;
import com.hsgene.hdas.cmcs.modules.admin.service.IPublicConfigService;
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
public class PublicConfigServiceImpl extends BaseServiceImpl<Instance> implements IPublicConfigService {

    @Autowired
    PublicConfigMapper mapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RoleClassMapper roleClassMapper;

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
    public PageInfo<Map<String, Object>> selectPropertiesByPage(HttpServletRequest hsr, long maxClassId) {
        return dealSelectPropertiesOfPage(hsr, maxClassId);
    }

    @Override
    public PageInfo<Map<String, Object>> selectPropertiesByKeyPage(HttpServletRequest hsr, long maxClassId) {
        return dealSelectPropertiesOfPage(hsr, maxClassId);
    }

    @Override
    public List<Map<String, Object>> getAllKeyAndValue(HttpServletRequest hsr, String username) {
        long userId = userMapper.getIdByName(username);
        long maxClassId = roleClassMapper.getMaxClassIdByUserId(userId);
        if ("admin".equals(username)) {
            maxClassId = 2;
        }
        long versionId = Long.valueOf(hsr.getParameter("versionId"));
        long envId = Long.valueOf(hsr.getParameter("envId"));
        Map<String, Object> map = new HashMap<>(16);
        map.put("envId", envId);
        map.put("versionId", versionId);
        return dealDataByClassId(map, maxClassId);
    }

    @Override
    public List<Map<String, Object>> getDuplicateProductAndModuleAndEnvByResourceIds(long[] resourceIds) {
        return mapper.getDuplicateProductAndModuleAndEnvByResourceIds(resourceIds);
    }

    public List<Map<String, Object>> dealDataByClassId(Map<String, Object> map, long maxClassId) {
        List<Map<String, Object>> listMap = mapper.getAllKeyAndValue(map);
        for (Map<String, Object> tempMap : listMap) {
            long classId = (long) tempMap.get("class_id");
            if (maxClassId < classId) {
                tempMap.put("value", "******");
            }
        }
        return listMap;
    }

    public PageInfo<Map<String, Object>> dealSelectPropertiesOfPage(HttpServletRequest hsr, long maxClassId) {
        int draw = Integer.valueOf(hsr.getParameter("draw") == null ? "1" : hsr.getParameter("draw"));
        int start = Integer.valueOf(hsr.getParameter("start") == null ? "0" : hsr.getParameter("start"));
        int pageSize = Integer.valueOf(hsr.getParameter("length") == null ? "10" : hsr.getParameter("length"));
        long versionId = Long.valueOf(hsr.getParameter("versionId") == null ? "-1" : hsr.getParameter("versionId"));
        long envId = Long.valueOf(hsr.getParameter("envId") == null ? "-1" : hsr.getParameter("envId"));
        String key = hsr.getParameter("name");
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
            if (envId != -1) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("envId", envId);
                page = mapper.selectPropertiesByKeyPage(map);
            } else {
                page = mapper.selectPropertiesByPage(versionId);
            }
        } else {
            Map<String, Object> map = new HashMap<>(16);
            map.put("versionId", versionId);
            map.put("key", key);
            map.put("envId", envId);
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
