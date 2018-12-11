package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Role;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IRoleAuthResourceService;
import com.hsgene.hdas.cmcs.modules.admin.service.IRoleClassService;
import com.hsgene.hdas.cmcs.modules.admin.service.IRoleService;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserRoleService;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 角色控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/11 17:39
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IRoleAuthResourceService roleAuthResourceService;

    @Autowired
    private IUserRoleService userRoleService;

    @Autowired
    private IRoleClassService roleClassService;

    /**
     * @param
     * @return java.lang.String  角色新增页名字
     * @description 获取角色新增页
     * @author maodi
     * @createDate 2018/6/13 15:32
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/role_add";
    }

    /**
     * @param
     * @return java.lang.String 角色修改页名字
     * @description 获取角色修改页
     * @author maodi
     * @createDate 2018/6/13 15:33
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/role_update";
    }

    /**
     * @param
     * @return java.lang.Object  角色数据，id、name的键值对组成的链表
     * @description 获取角色数据，为下拉框提供数据
     * @author maodi
     * @createDate 2018/6/13 15:33
     */
    @RequestMapping("/role_data")
    public @ResponseBody
    Object getSelectByMap() {
        try {
            return roleService.getSelectByMap();
        } catch (Exception e) {
            log.error("获取角色数据，为下拉框提供数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取角色数据，为下拉框提供数据出错");
        }
    }

    /**
     * @param role 角色
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse  新增结果
     * @description 新增角色
     * @author maodi
     * @createDate 2018/6/13 15:34
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(Role role) {
        ConfigResponse configResponse;
        try {
            String name = role.getName();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", name);
            int num = roleService.countByMap(map);
            if (num > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "添加失败！跟其他角色名重复");
            } else {
                role.setId(StringUtil.getId());
                roleService.save(role);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("新增角色出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "新增角色出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object 角色分页数据
     * @description 根据名字获取角色分页数据
     * @author maodi
     * @createDate 2018/6/13 14:59
     */
    @RequestMapping("/query_by_name")
    public @ResponseBody
    Object queryByNamePage(HttpServletRequest hsr) {
        try {
            return roleService.selectByNamePage(hsr);
        } catch (Exception e) {
            log.error("根据名字获取角色分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据名字获取角色分页数据出错");
        }
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object  角色分页数据
     * @description 获取角色分页数据
     * @author maodi
     * @createDate 2018/6/13 15:34
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            return roleService.selectByPage(hsr);
        } catch (NumberFormatException e) {
            log.error("获取角色分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取角色分页数据出错");
        }
    }

    /**
     * @param role 角色
     * @return java.lang.Object 修改结果
     * @description 修改角色
     * @author maodi
     * @createDate 2018/6/13 15:36
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(Role role) {
        ConfigResponse configResponse;
        try {
            String name = role.getName();
            long id = role.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("name", name);
            map.put("id", id);
            int organNum = roleService.countByMap(map);
            if (organNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其他角色名重复");
            } else {
                roleService.update(role);
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改角色出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改角色出错");
        }
        return configResponse;
    }

    /**
     * @param ids 角色ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除角色
     * @author maodi
     * @createDate 2018/6/13 15:37
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            roleClassService.deleteByRoleIds(ids);
            roleService.deleteByIds(ids);
            roleAuthResourceService.deleteByRoleIds(ids);
            userRoleService.deleteByRoleIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除角色出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除角色出错");
        }
    }

    /**
     * @param roleId 角色id
     * @return java.lang.Object 安全级别id
     * @description 根据角色获取安全级别id
     * @author maodi
     * @createDate 2018/9/7 16:15
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/get_classId_by_roleId")
    public @ResponseBody
    Object getClassIdByRoleId(long roleId) {
        try {
            return new ConfigResponse(HttpStatus.OK.value(), roleClassService.getClassIdByRoleId(roleId),
                    "根据角色获取级别成功！");
        } catch (Exception e) {
            log.error("根据角色获取级别出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据角色获取级别出错");
        }
    }

}
