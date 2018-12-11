package com.hsgene.hdas.cmcs.modules.admin.util;

import com.github.pagehelper.PageHelper;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.util
 * @author: maodi
 * @createDate: 2018/6/5 9:17
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class PageInfoUtil {

    /**
     * @param pageInfo
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo
     * @description 添加序号到pageinfo中
     * @author maodi
     * @createDate 2018/6/5 9:28
     */
    public static PageInfo addNumToList(PageInfo<Map<String, Object>> pageInfo) {
        List<Map<String, Object>> data = pageInfo.getData();
        List<Map<String, Object>> dealData = new ArrayList<>();
        long pageNum = pageInfo.getPageNum();
        long pageSize = pageInfo.getPageSize();
        for (int i = 0, size = data.size(); i < size; i++) {
            Map<String, Object> map = data.get(i);
            long num = (pageNum - 1) * pageSize + i + 1;
            map.put("num", num);
            dealData.add(map);
        }
        return new PageInfo(dealData);
    }

    /**
     * @param listMap 分页的数据
     * @param draw    前台datatable传来的draw，要跟前台传过来的保持一致，就直接使用前台传过来的，否则表格显示有问题
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 处理PageInfo<Map<String, Object>>，将draw，pageNum等数据加入PageInfo
     * @author maodi
     * @createDate 2018/9/7 16:24
     */
    public static PageInfo<Map<String, Object>> dealListMap(List<Map<String, Object>> listMap, int draw) {
        PageHelper.startPage(1, Integer.MAX_VALUE);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(listMap);
        pageInfo.setDraw(draw);
        pageInfo.setPageNum(1);
        PageInfoUtil.addNumToList(pageInfo);
        return pageInfo;
    }

}
