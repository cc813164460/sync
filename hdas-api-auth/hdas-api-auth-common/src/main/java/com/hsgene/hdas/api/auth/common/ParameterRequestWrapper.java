package com.hsgene.hdas.api.auth.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 实现请求中的参数可以添加修改的类
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/20 13:32
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> params = new HashMap<>();

    public ParameterRequestWrapper(HttpServletRequest request) {
        //将request交给父类，以便于调用对应方法的时候，将其输出，其实父类的实现方式和第一种new的方式类似
        super(request);
        //将参数表，赋予给当前的Map以便于持有request中的参数
        this.params.putAll(request.getParameterMap());
    }

    /**
     * @param request      http请求
     * @param extendParams 扩展参数
     * @return
     * @description 重载一个构造方法
     * @author maodi
     * @createDate 2018/9/20 13:34
     */
    public ParameterRequestWrapper(HttpServletRequest request, Map<String, Object> extendParams) {
        this(request);
        //将扩展参数写入参数表
        addAllParameters(extendParams);
    }

    /**
     * @param name 参数名
     * @return java.lang.String
     * @description 重写getParameter，代表参数从当前类中的map获取
     * @author maodi
     * @createDate 2018/9/20 13:34
     */
    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    /**
     * @param
     * @return java.util.Map<java.lang.String,java.lang.String[]>
     * @description 获取参数的map
     * @author maodi
     * @createDate 2018/9/20 14:08
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    /**
     * @param name 参数名
     * @return java.lang.String[]
     * @description 根据参数名获取参数值数字
     * @author maodi
     * @createDate 2018/9/20 13:34
     */
    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    /**
     * @param otherParams 参数表
     * @return void
     * @description 增加多个参数
     * @author maodi
     * @createDate 2018/9/20 13:33
     */
    public void addAllParameters(Map<String, Object> otherParams) {
        for (Map.Entry<String, Object> entry : otherParams.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @param name 参数名字
     * @param value 参数值
     * @return void
     * @description 增加参数
     * @author maodi
     * @createDate 2018/9/20 13:33
     */
    public void addParameter(String name, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                params.put(name, (String[]) value);
            } else if (value instanceof String) {
                params.put(name, new String[]{(String) value});
            } else {
                params.put(name, new String[]{String.valueOf(value)});
            }
        }
    }

}
