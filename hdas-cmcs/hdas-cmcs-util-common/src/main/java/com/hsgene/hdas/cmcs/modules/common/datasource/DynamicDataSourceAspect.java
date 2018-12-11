package com.hsgene.hdas.cmcs.modules.common.datasource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @description: 动态数据切面类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.common.util
 * @author: maodi
 * @createDate: 2018/5/29 16:00
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Aspect
@Order(-1)// 保证该AOP在@Transactional之前执行
@Component
public  class DynamicDataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) throws Throwable {

        String dsId = targetDataSource.name();
        if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {
            logger.error("数据源[{}]不存在，使用默认数据源 > {}", targetDataSource.name(), point.getSignature());
        } else {
            logger.debug("Use DataSource : {} > {}", targetDataSource.name(), point.getSignature());
            DynamicDataSourceContextHolder.setDataSourceType(targetDataSource.name());
        }
    }

    @After("@annotation(targetDataSource)")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        logger.debug("Revert DataSource : {} > {}", targetDataSource.name(), point.getSignature());
        DynamicDataSourceContextHolder.clearDataSourceType();
    }

}
