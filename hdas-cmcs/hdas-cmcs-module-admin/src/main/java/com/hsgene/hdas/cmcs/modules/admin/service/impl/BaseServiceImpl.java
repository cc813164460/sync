package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;
import com.hsgene.hdas.cmcs.modules.admin.service.IBaseService;
import com.hsgene.hdas.cmcs.modules.admin.util.PageInfoUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description: 基本实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/6 10:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public abstract class BaseServiceImpl<T> implements IBaseService<T> {

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<T>
     * @description 获取对应实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:15
     */
    protected abstract BaseMapper<T> getBaseMapper();

    /**
     * @param t 实体
     * @return void
     * @description 新增实体
     * @author maodi
     * @createDate 2018/6/13 16:16
     */
    @Override
    public void save(T t) {
        getBaseMapper().save(t);
    }

    /**
     * @param ids
     * @return void
     * @description 根据ids删除对应数据
     * @author maodi
     * @createDate 2018/6/13 16:16
     */
    @Override
    public void deleteByIds(long[] ids) {
        getBaseMapper().deleteByIds(ids);
    }

    /**
     * @param t
     * @return void
     * @description 修改实体
     * @author maodi
     * @createDate 2018/6/13 16:16
     */
    @Override
    public void update(T t) {
        getBaseMapper().update(t);
    }

    /**
     * @param id 实体id
     * @return T
     * @description 根据id获取实体
     * @author maodi
     * @createDate 2018/6/13 16:17
     */
    @Override
    public T get(long id) {
        return getBaseMapper().get(id);
    }

    /**
     * @param
     * @return java.util.List<T>
     * @description 获取全部实体
     * @author maodi
     * @createDate 2018/6/13 16:18
     */
    @Override
    public List<T> getAll() {
        return getBaseMapper().getAll();
    }

    /**
     * @param map 键值对
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 根据map获取list<map>信息
     * @author maodi
     * @createDate 2018/6/13 16:18
     */
    @Override
    public List<Map<String, Object>> getSelectByMap(Map<String, Object> map) {
        return getBaseMapper().getSelectByMap(map);
    }

    /**
     * @param
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 获取list<map>信息
     * @author maodi
     * @createDate 2018/6/13 16:19
     */
    @Override
    public List<Map<String, Object>> getSelectByMap() {
        return getBaseMapper().getSelectByMap();
    }

    /**
     * @param hsr http请求，包含参数
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo<java.util.HashMap>
     * @description 根据名字获取分页数据
     * @author maodi
     * @createDate 2018/6/13 16:19
     */
    @Override
    public PageInfo<Map<String, Object>> selectByNamePage(HttpServletRequest hsr) {
        return dealSelectOfPage(hsr, true);
    }

    /**
     * @param hsr http请求，包含参数
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo<java.util.HashMap>
     * @description 获取分页数据
     * @author maodi
     * @createDate 2018/6/13 16:21
     */
    @Override
    public PageInfo<Map<String, Object>> selectByPage(HttpServletRequest hsr) {
        return dealSelectOfPage(hsr, false);
    }

    /**
     * @param map 查询条件键值对
     * @return int
     * @description 根据map获取数量
     * @author maodi
     * @createDate 2018/6/13 16:21
     */
    @Override
    public int countByMap(Map<String, Object> map) {
        return getBaseMapper().countByMap(map);
    }

    /**
     * @param name 名字
     * @return long  id
     * @description 根据名字获取id
     * @author maodi
     * @createDate 2018/6/13 16:22
     */
    @Override
    public long getIdByName(String name) {
        return getBaseMapper().getIdByName(name);
    }

    /**
     * @param hsr      http请求，包含参数
     * @param haveName 是否有name
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo<java.util.HashMap> 分页数据
     * @description 处理http请求，组装分页数据及是否根据name查询
     * @author maodi
     * @createDate 2018/6/13 16:22
     */
    public PageInfo<Map<String, Object>> dealSelectOfPage(HttpServletRequest hsr, boolean haveName) {
        int draw = Integer.valueOf(hsr.getParameter("draw") == null ? "1" : hsr.getParameter("draw"));
        int start = Integer.valueOf(hsr.getParameter("start") == null ? "0" : hsr.getParameter("start"));
        int pageSize = Integer.valueOf(hsr.getParameter("length") == null ? "10" : hsr.getParameter("length"));
        int pageNum = (start / pageSize) + 1;
        PageHelper.startPage(pageNum, pageSize);
        // 需要把Page包装成PageInfo对象才能序列化。该插件也默认实现了一个PageInfo
        Page<Map<String, Object>> page;
        if (haveName) {
            String name = hsr.getParameter("name");
            page = getBaseMapper().selectByNamePage(name);
        } else {
            page = getBaseMapper().selectByPage();
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(page);
        pageInfo.setDraw(draw);
        pageInfo.setPageNum(pageNum);
        PageInfoUtil.addNumToList(pageInfo);
        return pageInfo;
    }

}
