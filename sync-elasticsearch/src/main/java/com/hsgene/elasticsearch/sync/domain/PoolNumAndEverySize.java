package com.hsgene.elasticsearch.sync.domain;

import java.io.Serializable;

/**
 * @description:
 * @projectName: sync-elasticsearch
 * @package: com.hsgene.elasticsearch.sync.domain
 * @author: maodi
 * @createDate: 2018/12/6 14:03
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class PoolNumAndEverySize implements Serializable{

    private static final long serialVersionUID = 5861015878190768982L;

    /**
     * 拆分成部分数
     */
    private int poolNum;

    /**
     * 每部分的数量
     */
    private int everySize;

    public int getPoolNum() {
        return poolNum;
    }

    public void setPoolNum(int poolNum) {
        this.poolNum = poolNum;
    }

    public int getEverySize() {
        return everySize;
    }

    public void setEverySize(int everySize) {
        this.everySize = everySize;
    }
}
