package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.ItemResource;
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
public interface ItemResourceMapper extends BaseMapper<ItemResource> {

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
