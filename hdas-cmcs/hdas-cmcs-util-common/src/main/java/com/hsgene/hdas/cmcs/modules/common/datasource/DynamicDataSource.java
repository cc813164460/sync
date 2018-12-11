package com.hsgene.hdas.cmcs.modules.common.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @description: 动态数据源
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.common.util
 * @author: maodi
 * @createDate: 2018/5/29 16:00
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }

}
