package com.hsgene.hdas.cmcs.modules.admin.controller;

import com.hsgene.hdas.cmcs.modules.admin.domain.Organ;
import com.hsgene.hdas.cmcs.modules.admin.domain.OrganArea;
import com.hsgene.hdas.cmcs.modules.admin.domain.OrganAreas;
import com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse;
import com.hsgene.hdas.cmcs.modules.admin.service.IOrganAreaService;
import com.hsgene.hdas.cmcs.modules.admin.service.IOrganService;
import com.hsgene.hdas.cmcs.modules.admin.service.IUserOrganService;
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
 * @description: 部门分布控制类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.controller
 * @author: maodi
 * @createDate: 2018/6/8 10:40
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
@Controller
@RequestMapping(value = "/organ_area")
public class OrganAreaController {

    @Autowired
    private IOrganAreaService organAreaService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IUserOrganService userOrganService;

    /**
     * @param
     * @return java.lang.String  部门新增页名字
     * @description 获取部门新增页
     * @author maodi
     * @createDate 2018/6/13 14:46
     */
    @RequestMapping(value = "/add_page", method = RequestMethod.GET)
    public String addPage() {
        return "/organ_area_add";
    }

    /**
     * @param
     * @return java.lang.String 部门修改页名字
     * @description 获取部门修改页
     * @author maodi
     * @createDate 2018/6/13 14:47
     */
    @RequestMapping(value = "/update_page", method = RequestMethod.GET)
    public String updatePage() {
        return "/organ_area_update";
    }

    /**
     * @param organAreas 部门-分布组
     * @return com.hsgene.hdas.cmcs.modules.admin.response.ConfigResponse 新增结果
     * @description 新增部门
     * @author maodi
     * @createDate 2018/6/13 14:47
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/insert")
    public @ResponseBody
    ConfigResponse insert(OrganAreas organAreas) {
        ConfigResponse configResponse;
        try {
            String organName = organAreas.getOrganName();
            Map<String, Object> organMap = new HashMap<>(16);
            organMap.put("name", organName);
            int organNum = organService.countByMap(organMap);
            if (organNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "添加失败！跟其它部门重复");
            } else {
                long organId = StringUtil.getId();
                Organ organ = new Organ();
                organ.setId(organId);
                organ.setName(organName);
                organ.setCreateDateTime(StringUtil.getNowTimestamp());
                organService.save(organ);
                action(organId, organAreas.getAreaIds());
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "添加成功！");
            }
        } catch (Exception e) {
            log.error("添加部门出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "添加部门出错");
        }
        return configResponse;
    }

    /**
     * @param hsr http请求，包含参数
     * @return java.lang.Object  部门分页数据
     * @description 获取部门分页数据
     * @author maodi
     * @createDate 2018/6/13 14:48
     */
    @RequestMapping("/query")
    public @ResponseBody
    Object queryByPage(HttpServletRequest hsr) {
        try {
            return organAreaService.selectByPage(hsr);
        } catch (Exception e) {
            log.error("获取部门分页数据出错", e);
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "获取部门分页数据出错");
        }
    }

    /**
     * @param organAreas 部门-分布组
     * @return java.lang.Object  修改结果
     * @description 修改部门
     * @author maodi
     * @createDate 2018/6/13 14:50
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/update")
    public @ResponseBody
    Object update(OrganAreas organAreas) {
        ConfigResponse configResponse;
        try {
            String organName = organAreas.getOrganName();
            long organId = organAreas.getId();
            Map<String, Object> organMap = new HashMap<>(16);
            organMap.put("name", organName);
            organMap.put("id", organId);
            int organNum = organService.countByMap(organMap);
            if (organNum > 0) {
                configResponse = new ConfigResponse(HttpStatus.CONFLICT.value(), 1, "修改失败！跟其它部门重复");
            } else {
                Organ organ = new Organ();
                organ.setId(organId);
                organ.setName(organName);
                organService.update(organ);
                long[] ids = {organId};
                organAreaService.deleteByOrganIds(ids);
                action(organId, organAreas.getAreaIds());
                configResponse = new ConfigResponse(HttpStatus.OK.value(), 1, "修改成功！");
            }
        } catch (Exception e) {
            log.error("修改部门出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "修改部门出错");
        }
        return configResponse;
    }

    /**
     * @param ids 部门ids
     * @return java.lang.Object 删除结果
     * @description 根据ids删除部门
     * @author maodi
     * @createDate 2018/6/13 14:52
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor =
            Exception.class)
    @RequestMapping("/delete_by_ids")
    public @ResponseBody
    Object deleteByIds(long[] ids) {
        try {
            organService.deleteByIds(ids);
            organAreaService.deleteByOrganIds(ids);
            userOrganService.deleteByOrganIds(ids);
            return new ConfigResponse(HttpStatus.OK.value(), ids.length, "删除成功！");
        } catch (Exception e) {
            log.error("根据ids删除部门出错", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ConfigResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, "根据ids删除部门出错");
        }
    }

    /**
     * @param organId 部门id
     * @param areaIds 分布ids
     * @return void
     * @description 批量新增部门数据
     * @author maodi
     * @createDate 2018/6/13 15:31
     */
    private void action(long organId, long[] areaIds) {
        int num = 0;
        for (long areaId : areaIds) {
            long organAreaId = StringUtil.getId() + num++;
            OrganArea organArea = new OrganArea();
            organArea.setId(organAreaId);
            organArea.setOrganId(organId);
            organArea.setAreaId(areaId);
            organAreaService.save(organArea);
        }
    }

}
