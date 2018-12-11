package com.hsgene.hdas.cmcs.modules.admin.mapper;

import com.hsgene.hdas.cmcs.modules.admin.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @description: 项目mapper
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.mapper
 * @author: maodi
 * @createDate: 2018/5/30 10:34
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Repository
public interface ProductMapper extends BaseMapper<Product> {

    List<Map<String, Object>> getData();

}
