package com.hsgene.elasticsearch.sync.util;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @description: 连接池类
 * @projectName: sync_elasticsearch
 * @package: com.hsgene.elasticsearch.sync.util
 * @author: maodi
 * @createDate: 2018/12/4 17:25
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Pool<T> implements Cloneable {

    protected GenericObjectPool<T> internalPool;

    public Pool() {
        super();
    }

    public Pool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) throws Exception {
        initPool(poolConfig, factory);
    }

    public void initPool(final GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) throws Exception {
        if (this.internalPool != null) {
            try {
                closeInternalPool();
            } catch (Exception e) {
                throw new Exception("初始化连接池时出错，先关闭连接池出错", e);
            }
        }
        this.internalPool = new GenericObjectPool<T>(factory, poolConfig);
    }

    protected void closeInternalPool() throws Exception {
        try {
            internalPool.close();
        } catch (Exception e) {
            throw new Exception("关闭连接池出错", e);
        }
    }

    public T getResource() throws Exception {
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            throw new Exception("获取连接池连接出错", e);
        }
    }

    public void returnResource(final T resource) throws Exception {
        try {
            if (resource != null) {
                returnResourceObject(resource);
            }
        } catch (Exception e) {
            throw new Exception("归还连接到连接池出错", e);
        }
    }

    private void returnResourceObject(final T resource) throws Exception {
        if (resource == null) {
            return;
        }
        try {
            internalPool.returnObject(resource);
        } catch (Exception e) {
            throw new Exception("归还连接到连接池出错", e);
        }
    }

    public void returnBrokenResource(final T resource) throws Exception {
        try {
            if (resource != null) {
                returnBrokenResourceObject(resource);
            }
        } catch (Exception e) {
            throw new Exception("归还中断连接到连接池出错", e);
        }
    }

    private void returnBrokenResourceObject(T resource) throws Exception {
        try {
            internalPool.invalidateObject(resource);
        } catch (Exception e) {
            throw new Exception("归还中断连接到连接池出错", e);
        }
    }

    public void destroy() throws Exception {
        try {
            closeInternalPool();
        } catch (Exception e) {
            throw new Exception("销毁连接池出错", e);
        }
    }

    public int getNumActive() throws Exception {
        try {
            if (poolInactive()) {
                return -1;
            }
            return this.internalPool.getNumActive();
        } catch (Exception e) {
            throw new Exception("获取活跃连接数出错", e);
        }
    }

    public int getNumIdle() throws Exception {
        try {
            if (poolInactive()) {
                return -1;
            }
            return this.internalPool.getNumIdle();
        } catch (Exception e) {
            throw new Exception("获取空闲连接数出错", e);
        }
    }

    public int getNumWaiters() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getNumWaiters();
    }

    public long getMeanBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getMeanBorrowWaitTimeMillis();
    }

    public long getMaxBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }
        return this.internalPool.getMaxBorrowWaitTimeMillis();
    }

    private boolean poolInactive() {
        return this.internalPool == null || this.internalPool.isClosed();
    }

    public void addObjects(int count) throws Exception {
        try {
            for (int i = 0; i < count; i++) {
                this.internalPool.addObject();
            }
        } catch (Exception e) {
            throw new Exception("Error trying to add idle objects", e);
        }
    }
}