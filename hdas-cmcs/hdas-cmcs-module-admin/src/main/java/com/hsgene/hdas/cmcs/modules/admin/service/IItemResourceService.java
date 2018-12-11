package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.ItemResource;

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
public interface IItemResourceService extends IBaseService<ItemResource> {

    void overByItemIds(long[] itemIds);

    void setUseByItemIds(long[] itemIds);

    void deleteByItemIds(long[] itemIds);

    void deleteByProductIds(long[] productIds);

    void deleteByModuleIds(long[] moduleIds);

    void deleteByEnvIds(long[] envIds);

    void deleteByVersionIds(long[] versionIds);

    List<Map<String, Object>> getProductModuleEnvList(Map<String, Object> map);

    long[] getResourceIdsByItemId(long itemId);

}
