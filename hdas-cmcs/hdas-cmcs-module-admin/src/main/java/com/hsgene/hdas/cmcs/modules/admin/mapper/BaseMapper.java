package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;
import java.util.Map;

/**
 * @description: 基本mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/6 9:27
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {

    /**
     * @param t
     * @return void
     * @description 新增保存实体
     * @author maodi
     * @createDate 2018/6/13 16:05
     */
    void save(T t);

    /**
     * @param t
     * @return void
     * @description 修改实体
     * @author maodi
     * @createDate 2018/6/13 16:05
     */
    void update(T t);

    /**
     * @param ids
     * @return void
     * @description 根据id数组删除
     * @author maodi
     * @createDate 2018/6/6 10:39
     */
    void deleteByIds(long[] ids);

    /**
     * @param name
     * @return com.github.pagehelper.Page<java.util.HashMap>
     * @description 根据name查找分页数据
     * @author maodi
     * @createDate 2018/6/6 10:39
     */
    Page<Map<String, Object>> selectByNamePage(@Param(value = "name") String name);

    /**
     * @param
     * @return com.github.pagehelper.Page<java.util.HashMap>
     * @description 查找分页数据
     * @author maodi
     * @createDate 2018/6/6 10:40
     */
    Page<Map<String, Object>> selectByPage();

    /**
     * @param id
     * @return T
     * @description 根据id查询实体
     * @author maodi
     * @createDate 2018/6/11 17:06
     */
    T get(@Param(value = "id") long id);

    /**
     * @param
     * @return java.util.List<T>
     * @description 查询所有实体
     * @author maodi
     * @createDate 2018/6/11 17:06
     */
    List<T> getAll();

    /**
     * @param map
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 根据键值对内容查询键值对列表
     * @author maodi
     * @createDate 2018/6/11 18:00
     */
    List<Map<String, Object>> getSelectByMap(Map<String, Object> map);

    /**
     * @param
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @description 查询所有键值对列表
     * @author maodi
     * @createDate 2018/6/11 17:07
     */
    List<Map<String, Object>> getSelectByMap();

    /**
     * @param map
     * @return int
     * @description 获取键值对条件的结果数量
     * @author maodi
     * @createDate 2018/6/12 11:17
     */
    int countByMap(Map<String, Object> map);

    /**
     * @param name
     * @return long
     * @description 根据名字获取id
     * @author maodi
     * @createDate 2018/6/13 14:10
     */
    long getIdByName(@Param(value = "name") String name);

}
