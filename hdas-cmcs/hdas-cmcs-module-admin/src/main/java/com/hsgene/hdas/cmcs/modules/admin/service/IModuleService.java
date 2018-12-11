package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.Module;

/**
 * @description: 环境接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/12 9:09
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IModuleService extends IBaseService<Module> {

    void deleteNotInIds(long[] ids);

}
