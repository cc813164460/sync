package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.Item;

import java.sql.Timestamp;
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
public interface IItemService extends IBaseService<Item> {

    void setUseByIds(long[] ids);

    void deleteAndUpdateTimeByIds(long[] ids);

    long[] getUseIdsByIds(long[] ids);

    void overByIds(long[] ids);

    void releaseFromLogByIds(Map<String, Object> map);

    void releaseByIds(Map<String, Object> map);

    void offlineFromLogByIds(Map<String, Object> map);

    void offlineByIds(Map<String, Object> map);

    Map<String, Object> getUseByVersionNumBack(String versionNumBack);

    List<Map<String, Object>> getPropertiesByIds(long[] ids);

    List<Map<String, Object>> getExcludePropertiesByInstanceId(long instanceId);

    List<Map<String, Object>> getExcludePropertiesByResourceId(long resourceId);

    List<Item> getPublicItemByInstanceTypeIdAndVersionId(Map<String, Object> map);

    List<Item> getByVersionIdAndReleaseStatus(long versionId, int releaseStatus);

    List<Item> getByIdsAndIsDeleteAndLastUpdateTime(long[] ids, String lastUpdateTime, String isDelete);

    List<Item> getByIds(long[] ids);

    long[] getIdsByVersionNumBacks(String[] ids);

    long[] getUseIdsByVersionNumBacks(String[] ids);

    String[] getVersionNumBacksByIds(long[] ids);

    Timestamp getLastUpdateTimeByIds(long[] ids);

    String[] getKeysByInstanceId(long instanceId);

    long[] getIdsByInstanceIdAndKeys(Map<String, Object> map);

    String[] getPublicMainKeysByInstanceTypeId(long instanceTypeId);

    long[] getPublicGrayIdsByInstanceTypeIdAndKeys(Map<String, Object> map);

    void updateToGrayReleasing(long[] itemIds);

    void updateToGrayReleased(long[] itemIds, Timestamp lastUpdateTime);

    int duplicateCurrentCountByMap(Map<String, Object> map);

    Item getDuplicateProductItemByMap(Map<String, Object> map);

    int duplicatePublicCountByMap(Map<String, Object> map);

    Item getDuplicatePublicItemByMap(Map<String, Object> map);

    long[] getDuplicateProductResourceIdsByMap(Map<String, Object> map);

    long getClassIdById(long id);

    Item[] getDuplicateItemByMap(Map<String, Object> map);

    int countProduct(Map<String, Object> map);

    int countPublic(Map<String, Object> map);

    String getValueByMap(Map<String, Object> map);

}
