package com.hsgene.hdas.api.auth.common;

import java.util.Set;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/27 16:07
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface IDatabaseUtil {

    AccessKeyAndSecretKey createKeys() throws Exception;

    AccessKeyAndSecretKey get(String accessKey) throws Exception;

    Set<AccessKeyAndSecretKey> getAllKeys() throws Exception;

    Set<AccessKeyAndSecretKey> getKeysByProductTagAndModuleTag(String productTag, String moduleTag) throws Exception;

    Set<AccessKeyAndSecretKey> getKeysByProductTag(String productTag) throws Exception;

    Set<AccessKeyAndSecretKey> getKeysByModuleTag(String moduleTag) throws Exception;

}
