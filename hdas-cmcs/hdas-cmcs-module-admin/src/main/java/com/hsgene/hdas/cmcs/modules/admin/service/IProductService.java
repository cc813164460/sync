package com.hsgene.hdas.cmcs.modules.admin.service;

import com.hsgene.hdas.cmcs.modules.admin.domain.Product;

import java.util.List;
import java.util.Map;

/**
 * @description: 项目接口
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/6/6 9:44
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IProductService extends IBaseService<Product> {

    List<Map<String, Object>> getData();

}
