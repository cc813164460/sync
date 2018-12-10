package com.hsgene.elasticsearch.sync.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 套餐
 * @projectName: hdas-geneshop-server
 * @package: com.hsgene.product.dto
 * @author: maodi
 * @createDate: 2018/10/22 15:44
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Product implements Serializable {

    private static final long serialVersionUID = -5728128023460529602L;

    /**
     * 套餐id
     */
    private String id;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 药物名称和id数组
     */
    private Medicine[] medicines;


    /**
     * 靶标(基因)（位点）
     */
    private String targets;

    /**
     * 基因检测内容
     */
    private String detection;

    /**
     * 临床意义
     */
    private String clinical;

    /**
     * 价格
     */
    private Float price;

    /**
     * 佣金
     */
    private Integer commission;

    /**
     * 疾病数组
     */
    private Cancer[] cancers;


    /**
     * 药物性质(字典)(治疗类型)（检测类型）
     */
    private String nature;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 机构(逗号拼接)
     */
    private String orgs;

    /**
     * 使用计数
     */
    private Integer usageCount;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 修改时间
     */
    @JsonDeserialize(using = DateJsonDeserializer.class)
    private Date updateDateTime;

    /**
     * 购物车状态(1-存在于购物车;2-不存在)
     */
    private Integer cartStatus;

    /**
     * 检测周期
     */
    private String testPeriod;

    /**
     * 积分
     */
    private Integer integration;

    /**
     * 政策类型（0政策内，1政策外）
     */
    private Integer policyType;

    /**
     * 是否被使用（0未使用，1已使用）
     */
    private Integer isUsed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Medicine[] getMedicines() {
        return medicines;
    }

    public void setMedicines(Medicine[] medicines) {
        this.medicines = medicines;
    }

    public String getTargets() {
        return targets;
    }

    public void setTargets(String targets) {
        this.targets = targets;
    }

    public String getDetection() {
        return detection;
    }

    public void setDetection(String detection) {
        this.detection = detection;
    }

    public String getClinical() {
        return clinical;
    }

    public void setClinical(String clinical) {
        this.clinical = clinical;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getCommission() {
        return commission;
    }

    public void setCommission(Integer commission) {
        this.commission = commission;
    }

    public Cancer[] getCancers() {
        return cancers;
    }

    public void setCancers(Cancer[] cancers) {
        this.cancers = cancers;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrgs() {
        return orgs;
    }

    public void setOrgs(String orgs) {
        this.orgs = orgs;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public Integer getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(Integer cartStatus) {
        this.cartStatus = cartStatus;
    }

    public String getTestPeriod() {
        return testPeriod;
    }

    public void setTestPeriod(String testPeriod) {
        this.testPeriod = testPeriod;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getPolicyType() {
        return policyType;
    }

    public void setPolicyType(Integer policyType) {
        this.policyType = policyType;
    }

    public Integer getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Integer isUsed) {
        this.isUsed = isUsed;
    }
}
