package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.OrganArea;
import org.springframework.stereotype.Repository;

/**
 * @description: 部门分布mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/8 10:45
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface OrganAreaMapper extends BaseMapper<OrganArea> {

    void deleteByOrganIds(long[] organIds);

    void deleteByAreaIds(long[] areaIds);

}
