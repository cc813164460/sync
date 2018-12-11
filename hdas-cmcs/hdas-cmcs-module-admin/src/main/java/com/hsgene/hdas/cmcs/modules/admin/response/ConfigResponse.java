package com.hsgene.hdas.cmcs.modules.admin.response;

import java.io.Serializable;

/**
 * @description: 返回结果实体类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.response
 * @author: maodi
 * @createDate: 2018/5/30 16:50
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ConfigResponse implements Serializable {

    private static final long serialVersionUID = 5156200761123727139L;
    /**
     * 返回结果code
     */
    private int code;

    /**
     * 返回数据(存json)
     */
    private Object info;

    /**
     * 结果描述
     */
    private String message;


    public ConfigResponse() {
    }

    public ConfigResponse(int code, Object info, String message) {
        this.code = code;
        this.info = info;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ConfigResponse{" +
               "code=" + code +
               ", info=" + info +
               ", message='" + message + '\'' +
               '}';
    }

}
