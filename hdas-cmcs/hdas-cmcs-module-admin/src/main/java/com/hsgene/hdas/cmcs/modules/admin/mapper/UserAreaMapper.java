package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.UserArea;
import org.springframework.stereotype.Repository;

/**
 * @description: 人员分布mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface UserAreaMapper extends BaseMapper<UserArea> {

    /**
     * @param userIds 人员ids
     * @return void
     * @description 根据userIds删除人员分布关系
     * @author maodi
     * @createDate 2018/6/13 16:09
     */
    void deleteByUserIds(long[] userIds);

    void deleteByAreaIds(long[] areaIds);

}
