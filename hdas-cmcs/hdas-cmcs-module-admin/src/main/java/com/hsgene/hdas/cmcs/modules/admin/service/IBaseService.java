package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.page.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description: 基本接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/6 9:29
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IBaseService<T> {

    /**
     * @param t 实体
     * @return void
     * @description 新增实体
     * @author maodi
     * @createDate 2018/6/13 16:52
     */
    void save(T t);

    /**
     * @param ids ids
     * @return void
     * @description 根据ids删除数据
     * @author maodi
     * @createDate 2018/6/13 16:52
     */
    void deleteByIds(long[] ids);

    /**
     * @param t 实体
     * @return void
     * @description 修改实体
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    void update(T t);

    /**
     * @param id 实体id
     * @return T
     * @description 根据id获取实体
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    T get(long id);

    /**
     * @param
     * @return java.util.List<T>
     * @description 获取所有实体
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    List<T> getAll();

    /**
     * @param map 查询条件键值对
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 根据map获取list<map>
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    List<Map<String, Object>> getSelectByMap(Map<String, Object> map);

    /**
     * @param
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 获取list<map>
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    List<Map<String, Object>> getSelectByMap();

    /**
     * @param hsr http请求，包含参数
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo<java.util.HashMap>
     * @description 根据name获取分页数据
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    PageInfo<Map<String, Object>> selectByNamePage(HttpServletRequest hsr);

    /**
     * @param hsr http请求，包含参数
     * @return com.hsgene.hdas.cmcs.modules.admin.page.PageInfo<java.util.HashMap>
     * @description 获取分页数据
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    PageInfo<Map<String, Object>> selectByPage(HttpServletRequest hsr);

    /**
     * @param map 查询条件键值对
     * @return int
     * @description 根据map获取数量
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    int countByMap(Map<String, Object> map);

    /**
     * @param name 名字
     * @return long
     * @description 根据name获取id
     * @author maodi
     * @createDate 2018/6/13 16:53
     */
    long getIdByName(String name);

}
