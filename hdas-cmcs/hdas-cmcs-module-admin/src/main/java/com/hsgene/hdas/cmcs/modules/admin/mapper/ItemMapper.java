package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.Item;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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
public interface ItemMapper extends BaseMapper<Item> {

    void deleteAndUpdateTimeByIds(@Param("ids") long[] ids, @Param("lastUpdateTime") String lastUpdateTime);

    void setUseByIds(@Param("ids") long[] ids, @Param("lastUpdateTime") String lastUpdateTime);

    long[] getUseIdsByIds(long[] ids);

    void overByIds(@Param("ids") long[] ids, @Param("lastUpdateTime") String lastUpdateTime);

    void releaseFromLogByIds(Map<String, Object> map);

    void releaseByIds(Map<String, Object> map);

    void offlineFromLogByIds(Map<String, Object> map);

    void offlineByIds(Map<String, Object> map);

    List<Map<String, Object>> getPropertiesByIds(long[] ids);

    List<Map<String, Object>> getExcludePropertiesByInstanceId(long instanceId);

    List<Map<String, Object>> getExcludePropertiesByResourceId(long resourceId);

    List<Item> getPublicItemByInstanceTypeIdAndVersionId(Map<String, Object> map);

    List<Item> getByVersionIdAndReleaseStatus(@Param("versionId") long versionId, @Param("releaseStatus") int
            releaseStatus);

    List<Item> getByIdsAndIsDeleteAndLastUpdateTime(@Param("ids") long[] ids, @Param("lastUpdateTime") String
            lastUpdateTime, @Param("isDelete") String isDelete);

    Map<String, Object> getUseByVersionNumBack(@Param("versionNumBack") String versionNumBack);

    List<Item> getByIds(@Param("ids") long[] ids);

    long[] getIdsByVersionNumBacks(String[] ids);

    long[] getUseIdsByVersionNumBacks(String[] ids);

    String[] getKeysByInstanceId(@Param("instanceId") long instanceId);

    String[] getPublicMainKeysByInstanceTypeId(@Param("instanceTypeId") long instanceTypeId);

    long[] getPublicGrayIdsByInstanceTypeIdAndKeys(Map<String, Object> map);

    long[] getIdsByInstanceIdAndKeys(Map<String, Object> map);

    String[] getVersionNumBacksByIds(@Param("ids") long[] ids);

    Timestamp getLastUpdateTimeByIds(long[] ids);

    void updateToGrayReleasing(long[] itemIds);

    void updateToGrayReleased(@Param("ids") long[] itemIds, @Param("lastUpdateTime") Timestamp lastUpdateTime);

    int duplicateCurrentCountByMap(Map<String, Object> map);

    Item getDuplicateProductItemByMap(Map<String, Object> map);

    long[] getDuplicateProductResourceIdsByMap(Map<String, Object> map);

    int duplicatePublicCountByMap(Map<String, Object> map);

    Item getDuplicatePublicItemByMap(Map<String, Object> map);

    long getClassIdById(@Param(value = "id") long id);

    Item[] getDuplicateItemByMap(Map<String, Object> map);

    int countProduct(Map<String, Object> map);

    int countPublic(Map<String, Object> map);

    String getValueByMap(Map<String, Object> map);
}
