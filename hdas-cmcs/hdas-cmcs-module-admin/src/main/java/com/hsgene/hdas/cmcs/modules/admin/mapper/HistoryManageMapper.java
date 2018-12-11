package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.github.pagehelper.Page;
import com.hsgene.hdas.cmcs.modules.admin.domain.Instance;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @description: 分布mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/6/11 17:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface HistoryManageMapper extends BaseMapper<Instance> {

    Page<Map<String, Object>> getHistory(Map<String, Object> map);

    Page<Map<String, Object>> getHistoryByCondition(Map<String, Object> map);

}
