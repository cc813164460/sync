package com.hsgene.hdas.api.auth.common;

import com.alibaba.fastjson.JSONObject;
import com.github.wnameless.json.flattener.JsonFlattener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: api鉴权使用工具类
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/18 10:31
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ApiAuthUse {

    /**
     * 日期时间格式
     */
    private final static String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 北京时间日期格式
     */
    private final static String PATTERN_BEIJING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'+08:00";

    /**
     * MD5字符串
     */
    private final static String MD5 = "MD5";

    /**
     * HmacSHA256字符串
     */
    private final static String HMAC_SHA256 = "HmacSHA256";

    /**
     * 默认使用编码为utf-8
     */
    private final static String ENCODE = "utf-8";

    /**
     * 时间戳超时时间5分钟（300000毫秒）
     */
    private final static long TIMESTAMP_TIMEOUT = 300000;

    /**
     * 存放内存中access_key和secret_key的map
     */
    private static Map<String, Object> keyMap = new HashMap<>();

    /**
     * @param url 经过排序拼接，添加secret_key，MD5加密过后的url字符串
     * @return java.lang.String
     * @description 计算经过MD5加密的url字符串的HmacSHA256值
     * @author maodi
     * @createDate 2018/9/18 10:36
     */
    private static String getHmacSHA256(String url, String key) throws Exception {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(ENCODE), HMAC_SHA256);
            mac.init(secretKey);
            String hash = byteToHex(mac.doFinal(url.getBytes(ENCODE)));
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw e;
        } catch (InvalidKeyException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw e;
        }
    }

    /**
     * @param b 字节数组
     * @return java.lang.String
     * @description 将加密后的HmacSHA256字节数组转换成字符串
     * @author maodi
     * @createDate 2018/9/20 16:21
     */
    public static String byteToHex(byte[] b) throws Exception {
        StringBuilder hs = new StringBuilder();
        String temp;
        for (int n = 0; b != null && n < b.length; n++) {
            temp = Integer.toHexString(b[n] & 0XFF);
            if (temp.length() == 1) {
                hs.append('0');
            }
            hs.append(temp);
        }
        return hs.toString();
    }

    /**
     * @param url 经过排序拼接，添加过secret_key过后的url字符串
     * @return java.lang.String
     * @description 对url进行MD5加密并全部大写
     * @author maodi
     * @createDate 2018/9/18 10:40
     */
    private static String getMD5EncryptAndUpperCase(String url) throws Exception {
        try {
            //获取一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance(MD5);
            byte[] result = digest.digest(url.getBytes());
            StringBuffer buffer = new StringBuffer();
            //把每一个byte和0xff做一个与运算
            for (byte b : result) {
                //与运算，加盐
                int number = b & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            //标准的MD5加密后的结果全部大写
            return StringUtils.upperCase(buffer.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param url       排序拼接过后的url字符串
     * @param secretKey 秘钥
     * @return java.lang.String
     * @description secretKey是登录的密码情况
     * @author maodi
     * @createDate 2018/9/21 8:50
     */
    private static String addSecretKey(String url, String secretKey) throws Exception {
        url += "&secret_key=" + secretKey;
        return url;
    }

    /**
     * @param request http请求
     * @return java.lang.String
     * @description 将http请求的数据（包括body中数据）参数进行组装
     * @author maodi
     * @createDate 2018/9/20 10:21
     */
    private static String getUrlByRequest(HttpServletRequest request) throws Exception {
        String url = request.getRequestURL().toString();
        Map<String, String[]> parameterMap = request.getParameterMap();
        int count = 0;
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                if (count++ == 0) {
                    url += "?";
                } else {
                    url += "&";
                }
                url += key + "=" + value;
            }
        }
        String body = IOUtils.toString(request.getInputStream(), "utf-8");
        if (StringUtils.isNotBlank(body)) {
            Map<String, Object> bodyMap = JsonFlattener.flattenAsMap(body);
            for (String key : bodyMap.keySet()) {
                url += "&" + key + "=" + bodyMap.get(key);
            }
        }
        return url;
    }

    /**
     * @param request http请求
     * @return java.lang.String
     * @description 根据request获取没有signature的url字符串
     * @author maodi
     * @createDate 2018/9/20 14:49
     */
    private static String getUrlNoSignatureByRequest(HttpServletRequest request) throws Exception {
        String url = request.getRequestURL().toString();
        Map<String, String[]> parameterMap = request.getParameterMap();
        int count = 0;
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            if (!"signature".equals(key)) {
                String[] values = entry.getValue();
                for (String value : values) {
                    if (count++ == 0) {
                        url += "?";
                    } else {
                        url += "&";
                    }
                    url += key + "=" + value;
                    break;
                }
            }
        }
        String body = IOUtils.toString(request.getInputStream(), "utf-8");
        if (StringUtils.isNotBlank(body)) {
            Map<String, Object> bodyMap = JsonFlattener.flattenAsMap(body);
            for (String key : bodyMap.keySet()) {
                url += "&" + key + "=" + bodyMap.get(key);
            }
        }
        return url;
    }

    /**
     * @param url 带参数请求url字符串
     * @return java.lang.String
     * @description 将参数进行排序并对blank的参数去除
     * @author maodi
     * @createDate 2018/9/18 10:52
     */
    private static String sortParameter(String url) throws Exception {
        String sortUrl;
        int questionMarkIndex = url.indexOf("?");
        if (questionMarkIndex != -1) {
            sortUrl = url.substring(0, questionMarkIndex + 1);
            url = url.substring(questionMarkIndex + 1, url.length());
        } else {
            throw new IllegalArgumentException("请求中没有参数");
        }
        if (StringUtils.isBlank(sortUrl)) {
            throw new IllegalArgumentException("请求格式有误");
        }
        if (StringUtils.isNotBlank(url)) {
            int andIndex = url.indexOf("&");
            if (andIndex != -1) {
                String[] parametersAndValues = url.split("&");
                if (parametersAndValues.length < 2) {
                    throw new IllegalArgumentException("请求中参数少于2个");
                }
                Map<String, Object> sortedMap = new TreeMap<>(Comparator.naturalOrder());
                for (String parameterAndValue : parametersAndValues) {
                    //parameterAndValue内容为blank的报错
                    if (StringUtils.isBlank(parameterAndValue)) {
                        throw new IllegalArgumentException("请求中参数没有key和value");
                    }
                    //没有=的去除
                    if (parameterAndValue.indexOf("=") != -1) {
                        String[] splits = parameterAndValue.split("=");
                        String parameter = splits[0];
                        //长度不为2的报错
                        if (splits.length == 2) {
                            String value = splits[1];
                            //key和value为blank的报错
                            if (StringUtils.isBlank(parameter)) {
                                throw new IllegalArgumentException("请求中参数为空");
                            }
                            if (StringUtils.isBlank(value)) {
                                sortedMap.put(parameter, "");
                            } else {
                                sortedMap.put(parameter, value);
                            }
                        } else {
                            sortedMap.put(parameter, "");
                        }
                    } else {
                        throw new IllegalArgumentException("请求中参数没有=");
                    }
                }
                int count = 0;
                int size = sortedMap.size();
                for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    sortUrl += key + "=" + value;
                    if (++count < size) {
                        sortUrl += "&";
                    }
                }
                return sortUrl;
            } else {
                throw new IllegalArgumentException("请求中参数不能少于2个");
            }
        }
        throw new IllegalArgumentException("请求中没有参数");
    }

    /**
     * @param request http请求
     * @return boolean
     * @description 获取请求中时间戳跟当前时间戳（毫秒）相差是否超时，是否超过现在时间
     * @author maodi
     * @createDate 2018/9/18 10:53
     */
    private static boolean judgeTimestamp(HttpServletRequest request) throws Exception {
        long timestamp;
        try {
            String date = request.getParameter("timestamp");
            if (StringUtils.isBlank(date)) {
                throw new IllegalArgumentException("请求中没有时间戳");
            }
            timestamp = dateToTimestamp(date);
            if (timestamp < 0) {
                throw new IllegalArgumentException("请求时间戳为负数");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("请求时间戳不是数字");
        }
        long nowTimestamp = System.currentTimeMillis();
        if (nowTimestamp - timestamp > TIMESTAMP_TIMEOUT) {
            throw new IllegalArgumentException("请求时间戳超时");
        }
        if (timestamp - nowTimestamp > 0) {
            throw new IllegalArgumentException("请求时间戳比现在时间戳大");
        }
        return true;
    }

    /**
     * @param request   http请求
     * @param secretKey HmacSHA256计算的secretKey
     * @return boolean
     * @description 判断请求中签名跟计算的签名是否一致
     * @author maodi
     * @createDate 2018/9/18 10:57
     */
    private static boolean judgeSignature(HttpServletRequest request, String secretKey) throws Exception {
        String signature = request.getParameter("signature");
        if (StringUtils.isBlank(signature)) {
            throw new IllegalArgumentException("请求中没有signature参数");
        }
        String url = getUrlNoSignatureByRequest(request);
        String nowSignature = getHmacSHA256(getMD5EncryptAndUpperCase(addSecretKey(sortParameter(url), secretKey)),
                secretKey);
        if (StringUtils.isBlank(nowSignature)) {
            throw new IllegalArgumentException("服务端无法根据请求计算签名");
        }
        if (!signature.equals(nowSignature)) {
            throw new IllegalArgumentException("请求签名和服务端计算签名不匹配");
        }
        return true;
    }

    /**
     * @param request     http请求
     * @param serverToken 服务端token令牌
     * @return boolean
     * @description 判断token是否有效
     * @author maodi
     * @createDate 2018/9/18 10:57
     */
    private static boolean judgeToken(HttpServletRequest request, String serverToken) throws Exception {
        String token = getToken(request);
        if (!token.equals(serverToken)) {
            throw new IllegalArgumentException("请求中token令牌与服务端token令牌不匹配");
        }
        return true;
    }

    /**
     * @param request http请求
     * @return java.lang.String
     * @description 根据url中的access_key获取secret_key
     * @author maodi
     * @createDate 2018/9/19 11:55
     */
    private static String getSecretKeyByRequestFromMap(HttpServletRequest request) throws Exception {
        String accessKey = request.getParameter("access_key");
        if (StringUtils.isBlank(accessKey)) {
            throw new IllegalArgumentException("请求没有access_key参数");
        }
        if (!keyMap.containsKey(accessKey) || keyMap.get(accessKey) == null || StringUtils.isBlank(keyMap.get
                (accessKey).toString())) {
            throw new IllegalArgumentException("该access_key没有对应的secret_key");
        }
        return keyMap.get(accessKey).toString();
    }

    /**
     * @param idu 获取keys的接口
     * @return com.hsgene.hdas.api.auth.domain.AccessKeyAndSecretKey
     * @description
     * @author maodi
     * @createDate 2018/9/27 9:08
     */
    public static void putAllKeysToMap(IDatabaseUtil idu) throws Exception {
        Set<AccessKeyAndSecretKey> set = idu.getAllKeys();
        for (AccessKeyAndSecretKey aas : set) {
            String accessKey = aas.getAccessKey();
            String secretKey = aas.getSecretKey();
            if (!keyMap.containsKey(accessKey)) {
                keyMap.put(accessKey, secretKey);
            }
        }
    }

    /**
     * @param url 带参数请求url字符串
     * @return void
     * @description 判断url基本格式是否正确
     * @author maodi
     * @createDate 2018/9/18 15:37
     */
    private static boolean judgeUrlIsComplete(String url) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("请求不能为空或者空字符串");
        }
        int questionMarkNum = url.length() - url.replace("?", "").length();
        if (questionMarkNum > 1) {
            throw new IllegalArgumentException("请求中不能超过1个?");
        }
        if (questionMarkNum < 1) {
            throw new IllegalArgumentException("请求中不能没有?");
        }
        int questionMarkIndex = url.indexOf("?");
        if (questionMarkIndex == url.length()) {
            throw new IllegalArgumentException("请求不能以?结尾");
        }
        if (questionMarkIndex == 0) {
            throw new IllegalArgumentException("请求不能以?开头");
        }
        int andMarkIndex = url.indexOf("&");
        if (andMarkIndex == -1) {
            throw new IllegalArgumentException("请求中不能没有&");
        }
        if (andMarkIndex == url.length()) {
            throw new IllegalArgumentException("请求不能以&结尾");
        }
        if (andMarkIndex == 0) {
            throw new IllegalArgumentException("请求不能以&开头");
        }
        if (andMarkIndex < questionMarkIndex) {
            throw new IllegalArgumentException("&不能在?前面");
        }
        return true;
    }

    /**
     * @param request       http请求
     * @param token         服务端的token令牌
     * @param permitUrlList permitUrlList为不进行鉴权的请求
     * @param idu           获取keys的接口
     * @return boolean
     * @description 判断url是否符合鉴权规则
     * @author maodi
     * @createDate 2018/9/19 11:54
     */
    public static boolean judgeRequestContainsAccessKey(HttpServletRequest request, String token, List<String>
            permitUrlList, IDatabaseUtil idu) throws Exception {
        try {
            //不用鉴权的直接放行
            if (!permitUrlList.contains(request.getRequestURL().toString())) {
                putAllKeysToMap(idu);
                String secretKey = getSecretKeyByRequestFromMap(request);
                return judgeAllRule(request, token, secretKey);
            }
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param request       http请求
     * @param token         服务端的token令牌
     * @param secretKey     secretKey为登录密码情况，作为秘钥
     * @param permitUrlList permitUrlList为不进行鉴权的请求
     * @return boolean
     * @description 判断url是否符合鉴权规则
     * @author maodi
     * @createDate 2018/9/21 8:53
     */
    public static boolean judgeRequestNoAccessKey(HttpServletRequest request, String token, String secretKey,
                                                  List<String> permitUrlList) throws Exception {
        try {
            //不用鉴权的直接放行
            if (!permitUrlList.contains(request.getRequestURL().toString())) {
                return judgeAllRule(request, token, secretKey);
            }
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @param request   http请求
     * @param token     服务端token令牌
     * @param secretKey 签名的秘钥
     * @return boolean
     * @description 判断是否符合所有的规则
     * @author maodi
     * @createDate 2018/9/21 8:58
     */
    private static boolean judgeAllRule(HttpServletRequest request, String token, String secretKey) throws Exception {
        String url = getUrlByRequest(request);
        if (judgeUrlIsComplete(url)) {
            if (judgeTimestamp(request)) {
                if (judgeToken(request, token)) {
                    if (judgeSignature(request, secretKey)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param request http请求
     * @param token   客户端的token
     * @param aas     accessKey和secretKey的实体
     * @return com.hsgene.hdas.api.auth.util.ParameterRequestWrapper
     * @description
     * @author maodi
     * @createDate 2018/9/27 9:13
     */
    public static ParameterRequestWrapper clientRequestHandle(HttpServletRequest request, String token,
                                                              AccessKeyAndSecretKey aas) throws Exception {
        String secretKey = aas.getSecretKey();
        ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper(request);
        parameterRequestWrapper.addParameter("access_key", aas.getAccessKey());
        parameterRequestWrapper.addParameter("token", token);
        parameterRequestWrapper.addParameter("timestamp", timestampToDate(System.currentTimeMillis()));
        String url = getUrlByRequest(parameterRequestWrapper);
        String signature = getHmacSHA256(getMD5EncryptAndUpperCase(addSecretKey(sortParameter(url), secretKey)),
                secretKey);
        parameterRequestWrapper.addParameter("signature", signature);
        return parameterRequestWrapper;
    }

    /**
     * @param request   http请求
     * @param token     客户端的token
     * @param secretKey 秘钥
     * @return com.hsgene.hdas.api.auth.util.ParameterRequestWrapper
     * @description
     * @author maodi
     * @createDate 2018/9/27 14:10
     */
    public static ParameterRequestWrapper clientRequestHandleNoAccessKey(HttpServletRequest request, String token,
                                                                         String secretKey) throws Exception {
        ParameterRequestWrapper parameterRequestWrapper = new ParameterRequestWrapper(request);
        parameterRequestWrapper.addParameter("token", token);
        parameterRequestWrapper.addParameter("timestamp", timestampToDate(System.currentTimeMillis()));
        String url = getUrlByRequest(parameterRequestWrapper);
        String signature = getHmacSHA256(getMD5EncryptAndUpperCase(addSecretKey(sortParameter(url), secretKey)),
                secretKey);
        parameterRequestWrapper.addParameter("signature", signature);
        return parameterRequestWrapper;
    }

    /**
     * @param request http请求
     * @return java.lang.String
     * @description 获取请求中的token
     * @author maodi
     * @createDate 2018/9/19 16:52
     */
    private static String getToken(HttpServletRequest request) throws Exception {
        String body = IOUtils.toString(request.getInputStream(), "utf-8");
        JSONObject bodyJson = JSONObject.parseObject(body);
        String headerToken = request.getHeader("token");
        String parameterToken = request.getParameter("token");
        //获取body中的token
        if (StringUtils.isNotBlank(body)) {
            String bodyToken = bodyJson.getString("token");
            if (StringUtils.isNotBlank(bodyToken)) {
                return bodyToken;
            }
        }
        //获取header中的token
        if (StringUtils.isNotBlank(headerToken)) {
            return headerToken;
        }
        //获取参数中的token
        else if (StringUtils.isNotBlank(parameterToken)) {
            return parameterToken;
        } else {
            throw new IllegalArgumentException("请求中没有token");
        }
    }

    /**
     * @param s 日期
     * @return java.lang.String
     * @description 将日期格式时间转化为时间戳（毫秒）
     * @author maodi
     * @createDate 2018/9/21 15:15
     */
    public static long dateToTimestamp(String s) throws Exception {
        try {
            if (s.endsWith("Z")) {
                s = s.replace("Z", "UTC");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN);
            Date date = simpleDateFormat.parse(s);
            return date.getTime();
        } catch (ParseException e) {
            throw new ParseException("日期时间格式有误", e.getErrorOffset());
        }
    }

    /**
     * @param timestamp 时间戳（毫秒）
     * @return java.lang.String
     * @description 将时间戳（毫秒）转化为日期时间格式
     * @author maodi
     * @createDate 2018/9/21 15:19
     */
    public static String timestampToDate(long timestamp) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_BEIJING);
        Date date = new Date(timestamp);
        String res = simpleDateFormat.format(date);
        return res;
    }

}
