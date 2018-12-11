package com.hsgene.hdas.cmcs.modules.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.service
 * @author: maodi
 * @createDate: 2018/8/9 17:05
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Service
public class RedisService {

    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param keys
     * @return void
     * @description 批量删除对应的value
     * @author maodi
     * @createDate 2018/9/7 16:46
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * @param pattern
     * @return void
     * @description 批量删除key
     * @author maodi
     * @createDate 2018/9/7 16:47
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * @param key
     * @return void
     * @description 删除对应的value
     * @author maodi
     * @createDate 2018/9/7 16:47
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * @param key
     * @return boolean
     * @description 判断缓存中是否有对应的value
     * @author maodi
     * @createDate 2018/9/7 16:47
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @return java.lang.Object
     * @description 读取缓存
     * @author maodi
     * @createDate 2018/9/7 16:48
     */
    public Object get(final String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * @param key
     * @return java.util.Set<java.lang.String>
     * @description 获取set
     * @author maodi
     * @createDate 2018/9/7 16:48
     */
    public Set<String> getSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * @param key
     * @return java.lang.Object
     * @description 根据key获取set
     * @author maodi
     * @createDate 2018/9/7 16:50
     */
    public Object getZSet(final String key) {
        return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return java.lang.Object
     * @description 根据key，start，end获取set
     * @author maodi
     * @createDate 2018/9/7 16:50
     */
    public Object getZSet(final String key, int start, int end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * @param key
     * @param value
     * @return boolean
     * @description 写入缓存
     * @author maodi
     * @createDate 2018/9/7 16:48
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param map
     * @return boolean
     * @description 设置hash
     * @author maodi
     * @createDate 2018/9/7 16:48
     */
    public boolean putAll(String key, Map<Object, Object> map) {
        boolean result = false;
        try {
            redisTemplate.opsForHash().putAll(key, map);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @return java.util.Map<java.lang.Object,java.lang.Object>
     * @description 根据key获取map
     * @author maodi
     * @createDate 2018/9/7 16:50
     */
    public Map<Object, Object> getAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key
     * @param list
     * @return boolean
     * @description 设置list
     * @author maodi
     * @createDate 2018/9/7 16:49
     */
    public boolean leftPushAll(final String key, List<Object> list) {
        boolean result = false;
        try {
            redisTemplate.opsForList().leftPushAll(key, list);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @return java.util.List<java.lang.Object>
     * @description 根据key获取list
     * @author maodi
     * @createDate 2018/9/7 16:51
     */
    public List<Object> getList(String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param key
     * @param value
     * @return boolean
     * @description 添加集合
     * @author maodi
     * @createDate 2018/9/7 16:49
     */
    public boolean addSet(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForSet().add(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param obj
     * @return boolean
     * @description set中添加set
     * @author maodi
     * @createDate 2018/9/7 16:51
     */
    public boolean addAll(String key, String... obj) {
        boolean result = false;
        try {
            redisTemplate.opsForSet().add(key, obj);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param set
     * @return boolean
     * @description 差集添加
     * @author maodi
     * @createDate 2018/9/7 16:52
     */
    public boolean differenceAndStore(String key, Set<Object> set) {
        boolean result = false;
        try {
            redisTemplate.opsForSet().differenceAndStore(key, set, key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @return java.util.Set<java.lang.String>
     * @description 获取set
     * @author maodi
     * @createDate 2018/9/7 16:52
     */
    public Set<String> members(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addZSet(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param key
     * @param value
     * @param expireTime
     * @return boolean
     * @description 写入缓存
     * @author maodi
     * @createDate 2018/9/7 16:49
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
