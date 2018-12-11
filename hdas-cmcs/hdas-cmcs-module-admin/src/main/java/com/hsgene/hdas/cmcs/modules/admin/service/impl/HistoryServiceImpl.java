package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hsgene.hdas.cmcs.modules.admin.domain.Instance;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.HistoryManageMapper;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;
import com.hsgene.hdas.cmcs.modules.admin.service.IHistoryManageService;
import com.hsgene.hdas.cmcs.modules.admin.util.PageInfoUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
public class HistoryServiceImpl extends BaseServiceImpl<Instance> implements IHistoryManageService {

    @Autowired
    HistoryManageMapper mapper;

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
    public PageInfo<Map<String, Object>> getHistory(HttpServletRequest hsr, long maxClassId) {
        return dealSelectPropertiesOfPage(hsr, maxClassId);
    }

    @Override
    public PageInfo<Map<String, Object>> getHistoryByCondition(HttpServletRequest hsr, long maxClassId) {
        return dealSelectPropertiesOfPage(hsr, maxClassId);
    }

    public PageInfo<Map<String, Object>> dealSelectPropertiesOfPage(HttpServletRequest request, long maxClassId) {
        int draw = Integer.valueOf(request.getParameter("draw") == null ? "1" : request.getParameter("draw"));
        int start = Integer.valueOf(request.getParameter("start") == null ? "0" : request.getParameter("start"));
        int pageSize = Integer.valueOf(request.getParameter("length") == null ? "10" : request.getParameter("length"));
        Map<String, Object> map = new HashMap<>(16);
        mapPut("name", map, request);
        mapPut("productId", map, request);
        mapPut("userId", map, request);
        mapPut("versionId", map, request);
        mapPut("releaseStatus", map, request);
        mapPut("isPublic", map, request);
        mapPut("startTime", map, request);
        mapPut("endTime", map, request);
        int pageNum = (start / pageSize) + 1;
        PageHelper.startPage(pageNum, pageSize);
        // 需要把Page包装成PageInfo对象才能序列化。该插件也默认实现了一个PageInfo
        Page<Map<String, Object>> page;
        if (map.size() == 2) {
            page = mapper.getHistory(map);
        } else {
            page = mapper.getHistoryByCondition(map);
        }
        for (Map<String, Object> tempMap : page) {
            long classId = (long) tempMap.get("class_id");
            if (maxClassId < classId) {
                tempMap.put("value", "******");
                tempMap.put("hasRole", "0");
            } else {
                tempMap.put("hasRole", "1");
            }
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(page);
        pageInfo.setDraw(draw);
        pageInfo.setPageNum(pageNum);
        PageInfoUtil.addNumToList(pageInfo);
        return pageInfo;
    }

    private void mapPut(String key, Map<String, Object> map, HttpServletRequest request) {
        Object obj = request.getParameter(key);
        if (obj != null && obj.toString().length() > 0) {
            String str = obj.toString();
            //去除前台传来的时间中有T
            if ("startTime".equals(key) || "endTime".equals(key)) {
                str = str.replace("T", " ");
            }
            if (StringUtils.isNotBlank(str)) {
                map.put(key, str);
            }
        }
    }

}
