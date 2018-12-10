package com.hsgene.elasticsearch.sync.domain;

import java.io.Serializable;

/**
 * @Description: 药物
 * @ProjectName: hdas-geneshop-server
 * @Package: com.hsgene.product.dto
 * @Author: FX
 * @CreateDate: 2018/11/13 11:06
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class Medicine implements Serializable {

    private static final long serialVersionUID = 8355469231024359120L;

    public Medicine() {
    }

    public Medicine(String medicineName, String medicineId) {
        this.medicineName = medicineName;
        this.medicineId = medicineId;
    }

    /**
     * 药物名称
     */
    private String medicineName;
    /**
     * 药物id
     */
    private String medicineId;

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }
}
