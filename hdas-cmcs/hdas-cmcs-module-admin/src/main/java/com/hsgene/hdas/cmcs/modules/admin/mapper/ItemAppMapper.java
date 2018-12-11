package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.ItemApp;
import org.springframework.stereotype.Repository;

import java.util.List;
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
public interface ItemAppMapper extends BaseMapper<ItemApp> {

    void deleteByAppIds(long[] appIds);

    void overByItemIds(long[] itemIds);

    void setUseByItemIds(long[] itemIds);

    void deleteByItemIds(long[] itemIds);

    long[] getItemIdsByAppId(Map<String, Object> map);

    List<Map<String, Object>> getItemIdAndAppIdsByItemIds(long[] itemIds);

    long[] getAppIdsByItemId(long itemId);

}
