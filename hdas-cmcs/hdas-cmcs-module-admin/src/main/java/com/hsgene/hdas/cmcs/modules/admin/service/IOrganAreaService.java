package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.OrganArea;

/**
 * @description: 部门分布关系接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/6 9:44
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IOrganAreaService extends IBaseService<OrganArea> {

    void deleteByOrganIds(long[] organIds);

    void deleteByAreaIds(long[] areaIds);

}
