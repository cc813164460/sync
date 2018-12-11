package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.UserOrgan;
import org.springframework.stereotype.Repository;

/**
 * @description: 人员部门mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface UserOrganMapper extends BaseMapper<UserOrgan> {

    /**
     * @param userIds 人员ids
     * @return void
     * @description 根据userIds删除人员部门关系
     * @author maodi
     * @createDate 2018/6/13 16:09
     */
    void deleteByUserIds(long[] userIds);

    void deleteByOrganIds(long[] organIds);

}
