package com.hsgene.hdas.api.auth.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @description: redis操作类
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/26 16:11
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Data
public class RedisUtil implements IDatabaseUtil {

    private String productTag;
    private String moduleTag;
    private String host;
    private int port;
    private int index;
    private String password;
    private String accessKey;
    private String secretKey;
    private boolean isPrint = false;

    private static Jedis jedis = null;

    public RedisUtil() {
    }

    public RedisUtil(String productTag, String moduleTag, String host, int port, int index, String password, boolean
            isPrint) {
        this.productTag = productTag;
        this.moduleTag = moduleTag;
        this.host = host;
        this.port = port;
        this.index = index;
        this.password = password;
        this.isPrint = isPrint;
    }

    public RedisUtil(String productTag, String moduleTag, String host, int port, int index, String password, String
            accessKey, String secretKey) {
        this.productTag = productTag;
        this.moduleTag = moduleTag;
        this.host = host;
        this.port = port;
        this.index = index;
        this.password = password;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private Jedis getJedis() {
        if (jedis == null || !jedis.isConnected()) {
            Jedis jedis = new Jedis(host, port);
            if (StringUtils.isNotBlank(password)) {
                jedis.auth(password);
            }
            jedis.select(index);
        }
        return jedis;
    }

    /**
     * @param
     * @return com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey
     * @description 创建accessKey和secretKey，如果存在就直接获取，没有就创建新的
     * @author maodi
     * @createDate 2018/9/26 17:55
     */
    @Override
    public AccessKeyAndSecretKey createKeys() throws Exception {
        Jedis jedis = getJedis();
        try {
            String accessKey = productTag + Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + UUID.randomUUID()
                    .toString();
            String secretKey = jedis.get(accessKey);
            if (StringUtils.isBlank(secretKey)) {
                secretKey = productTag + Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + UUID.randomUUID()
                        .toString();
                jedis.set(accessKey, secretKey);
            }
            if (isPrint) {
                Constant.printInfo(accessKey, secretKey);
            }
            return new AccessKeyAndSecretKey(accessKey, secretKey);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param
     * @return com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey
     * @description 根据accessKey获取secretKey
     * @author maodi
     * @createDate 2018/9/26 17:55
     */
    @Override
    public AccessKeyAndSecretKey get(String accessKey) throws Exception {
        Jedis jedis = getJedis();
        try {
            String secretKey = jedis.get(accessKey);
            if (StringUtils.isBlank(secretKey)) {
                throw new IllegalArgumentException("This accessKey has no secretKey.");
            }
            if (isPrint) {
                Constant.printInfo(accessKey, secretKey);
            }
            return new AccessKeyAndSecretKey(accessKey, secretKey);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 获取所有accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:54
     */
    @Override
    public Set<AccessKeyAndSecretKey> getAllKeys() throws Exception {
        String pattern = "*";
        return getKeysByPattern(pattern);
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据项目标识和模块标识获取accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:53
     */
    @Override
    public Set<AccessKeyAndSecretKey> getKeysByProductTagAndModuleTag(String productTag, String moduleTag) throws
            Exception {
        String pattern = productTag + Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + "*";
        return getKeysByPattern(pattern);
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据项目标识获取accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:52
     */
    @Override
    public Set<AccessKeyAndSecretKey> getKeysByProductTag(String productTag) throws Exception {
        String pattern = productTag + Constant.SEPARATOR + "*";
        return getKeysByPattern(pattern);
    }

    /**
     * @param
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据模块标识获取accessKey和secretKey
     * @author maodi
     * @createDate 2018/9/26 17:52
     */
    @Override
    public Set<AccessKeyAndSecretKey> getKeysByModuleTag(String moduleTag) throws Exception {
        String pattern = "*" + Constant.SEPARATOR + moduleTag + Constant.SEPARATOR + "*";
        return getKeysByPattern(pattern);
    }

    /**
     * @param pattern
     * @return java.util.Set<com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey>
     * @description 根据pattern获取set集合
     * @author maodi
     * @createDate 2018/9/27 15:15
     */
    public Set<AccessKeyAndSecretKey> getKeysByPattern(String pattern) throws Exception {
        Jedis jedis = getJedis();
        try {
            Set<String> keySet = jedis.keys(pattern);
            Set<AccessKeyAndSecretKey> accessKeyAndSecretKeySet = new HashSet<>();
            for (String accessKey : keySet) {
                String secretKey = jedis.get(accessKey);
                if (isPrint) {
                    Constant.printInfo(accessKey, secretKey);
                }
                accessKeyAndSecretKeySet.add(new AccessKeyAndSecretKey(accessKey, secretKey));
            }
            return accessKeyAndSecretKeySet;
        } catch (Exception e) {
            throw e;
        }
    }

}
