package com.hsgene.hdas.cmcs.modules.common.datasource;

import java.lang.annotation.*;

/**
 * @description: TargetDataSource注解
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.common.util
 * @author: maodi
 * @createDate: 2018/5/29 16:00
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    String name();

}

