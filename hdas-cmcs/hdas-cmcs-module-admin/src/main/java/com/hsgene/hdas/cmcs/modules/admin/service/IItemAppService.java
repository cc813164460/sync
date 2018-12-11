package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.ItemApp;

import java.util.List;
import java.util.Map;

/**
 * @description: 用户接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/11 17:02
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IItemAppService extends IBaseService<ItemApp> {

    void deleteByAppIds(long[] appIds);

    void overByItemIds(long[] itemIds);

    void setUseByItemIds(long[] itemIds);

    void deleteByItemIds(long[] itemIds);

    long[] getItemIdsByAppId(Map<String, Object> map);

    List<Map<String, Object>> getItemIdAndAppIdsByItemIds(long[] itemIds);

    long[] getAppIdsByItemId(long itemId);

}
