package com.hsgene.model;

/**
 * @author: maodi@hsgene.com
 * @Description:
 * @Date: Created in 11:28 2017/12/12
 * @Modified By:
 */
public class TargetInfo {
    private String type;
    private Object object;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "TargetInfo{" +
               "type='" + type + '\'' +
               ", object=" + object +
               '}';
    }
}
