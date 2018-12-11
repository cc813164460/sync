package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.Module;
import org.springframework.stereotype.Repository;

/**
 * @description: 环境mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/12 9:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface ModuleMapper extends BaseMapper<Module> {

    void deleteNotInIds(long[] ids);

}
