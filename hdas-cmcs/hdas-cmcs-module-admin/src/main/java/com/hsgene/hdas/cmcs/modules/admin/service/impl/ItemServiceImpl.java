package com.hsgene.hdas.cmcs.modules.admin.service.impl;

import com.hsgene.hdas.cmcs.modules.admin.domain.Item;
import com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper;
import com.hsgene.hdas.cmcs.modules.admin.mapper.ItemMapper;
import com.hsgene.hdas.cmcs.modules.admin.service.IItemService;
import com.hsgene.hdas.cmcs.modules.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @description: 条目实现类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service.impl
 * @author: maodi
 * @createDate: 2018/6/11 17:08
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class ItemServiceImpl extends BaseServiceImpl<Item> implements IItemService {

    @Autowired
    ItemMapper mapper;

    /**
     * @param
     * @return com.hsgene.hdas.cmcs.modules.admin.mapper.BaseMapper<com.hsgene.hdas.cmcs.modules.admin.domain.Item>
     * 当前实现的mapper
     * @description 获取当前实现的mapper
     * @author maodi
     * @createDate 2018/6/13 16:14
     */
    @Override
    protected BaseMapper<Item> getBaseMapper() {
        return mapper;
    }

    @Override
    public void deleteByIds(long[] ids) {
        mapper.deleteByIds(ids);
    }

    @Override
    public void deleteAndUpdateTimeByIds(long[] ids) {
        mapper.deleteAndUpdateTimeByIds(ids, StringUtil.getFormatTime());
    }

    @Override
    public void setUseByIds(long[] ids) {
        mapper.setUseByIds(ids, StringUtil.getFormatTime());
    }

    @Override
    public long[] getUseIdsByIds(long[] ids) {
        return mapper.getUseIdsByIds(ids);
    }

    @Override
    public void overByIds(long[] ids) {
        mapper.overByIds(ids, StringUtil.getFormatTime());
    }

    @Override
    public Map<String, Object> getUseByVersionNumBack(String versionNumBack) {
        return mapper.getUseByVersionNumBack(versionNumBack);
    }

    @Override
    public void releaseFromLogByIds(Map<String, Object> map) {
        mapper.releaseFromLogByIds(map);
    }

    @Override
    public void releaseByIds(Map<String, Object> map) {
        mapper.releaseByIds(map);
    }

    @Override
    public void offlineFromLogByIds(Map<String, Object> map) {
        mapper.offlineFromLogByIds(map);
    }

    @Override
    public void offlineByIds(Map<String, Object> map) {
        mapper.offlineByIds(map);
    }

    @Override
    public List<Map<String, Object>> getPropertiesByIds(long[] ids) {
        return mapper.getPropertiesByIds(ids);
    }

    @Override
    public List<Map<String, Object>> getExcludePropertiesByInstanceId(long instanceId) {
        return mapper.getExcludePropertiesByInstanceId(instanceId);
    }

    @Override
    public List<Map<String, Object>> getExcludePropertiesByResourceId(long resourceId) {
        return mapper.getExcludePropertiesByResourceId(resourceId);
    }

    @Override
    public List<Item> getPublicItemByInstanceTypeIdAndVersionId(Map<String, Object> map) {
        return mapper.getPublicItemByInstanceTypeIdAndVersionId(map);
    }

    @Override
    public List<Item> getByVersionIdAndReleaseStatus(long versionId, int releaseStatus) {
        return mapper.getByVersionIdAndReleaseStatus(versionId, releaseStatus);
    }

    @Override
    public long[] getPublicGrayIdsByInstanceTypeIdAndKeys(Map<String, Object> map) {
        return mapper.getPublicGrayIdsByInstanceTypeIdAndKeys(map);
    }

    @Override
    public List<Item> getByIdsAndIsDeleteAndLastUpdateTime(long[] ids, String lastUpdateTime, String isDelete) {
        return mapper.getByIdsAndIsDeleteAndLastUpdateTime(ids, lastUpdateTime, isDelete);
    }

    @Override
    public List<Item> getByIds(long[] ids) {
        return mapper.getByIds(ids);
    }

    @Override
    public long[] getIdsByVersionNumBacks(String[] ids) {
        return mapper.getIdsByVersionNumBacks(ids);
    }

    @Override
    public long[] getUseIdsByVersionNumBacks(String[] ids) {
        return mapper.getUseIdsByVersionNumBacks(ids);
    }

    @Override
    public String[] getVersionNumBacksByIds(long[] ids) {
        return mapper.getVersionNumBacksByIds(ids);
    }

    @Override
    public Timestamp getLastUpdateTimeByIds(long[] ids) {
        return mapper.getLastUpdateTimeByIds(ids);
    }

    @Override
    public void updateToGrayReleasing(long[] itemIds) {
        mapper.updateToGrayReleasing(itemIds);
    }

    @Override
    public void updateToGrayReleased(long[] itemIds, Timestamp lastUpdateTime) {
        mapper.updateToGrayReleased(itemIds, lastUpdateTime);
    }

    @Override
    public long[] getIdsByInstanceIdAndKeys(Map<String, Object> map) {
        return mapper.getIdsByInstanceIdAndKeys(map);
    }

    @Override
    public String[] getKeysByInstanceId(long instanceId) {
        return mapper.getKeysByInstanceId(instanceId);
    }

    @Override
    public String[] getPublicMainKeysByInstanceTypeId(long instanceTypeId) {
        return mapper.getPublicMainKeysByInstanceTypeId(instanceTypeId);
    }

    @Override
    public int duplicateCurrentCountByMap(Map<String, Object> map) {
        return mapper.duplicateCurrentCountByMap(map);
    }

    @Override
    public Item getDuplicateProductItemByMap(Map<String, Object> map) {
        return mapper.getDuplicateProductItemByMap(map);
    }

    @Override
    public long[] getDuplicateProductResourceIdsByMap(Map<String, Object> map) {
        return mapper.getDuplicateProductResourceIdsByMap(map);
    }

    @Override
    public int duplicatePublicCountByMap(Map<String, Object> map) {
        return mapper.duplicatePublicCountByMap(map);
    }

    @Override
    public Item getDuplicatePublicItemByMap(Map<String, Object> map) {
        return mapper.getDuplicatePublicItemByMap(map);
    }

    @Override
    public long getClassIdById(long id) {
        return mapper.getClassIdById(id);
    }

    @Override
    public Item[] getDuplicateItemByMap(Map<String, Object> map) {
        return mapper.getDuplicateItemByMap(map);
    }

    @Override
    public int countProduct(Map<String, Object> map) {
        return mapper.countProduct(map);
    }

    @Override
    public int countPublic(Map<String, Object> map) {
        return mapper.countPublic(map);
    }

    @Override
    public String getValueByMap(Map<String, Object> map) {
        return mapper.getValueByMap(map);
    }
}
