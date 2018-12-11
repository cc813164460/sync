package com.hsgene.hdas.cmcs.modules.api;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Set;


/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.api
 * @author: maodi
 * @createDate: 2018/7/17 10:29
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ReleaseListener {

    private final static Logger log = Logger.getLogger(ReleaseListener.class);

    private String serverIp;
    private String appId;
    private String appIp;
    private String appName;
    private String configUrl;
    private String registerInsertAppUrl;
    private String registerUpdateAppUrl;
    private String notificationId;
    private final static String CONFLICT_IP = "跟其它ip地址重复";
    private final static String SERVER_OFFLINE = "服务端关闭了连接";
    private final static String REGISTER_ERROR = "注册app出错";
    private final static String REQUEST_CONFIG_ERROR = "请求配置信息出错";
    private final static String ID_CONFLICT_IP = "该ip已经注册，该ip对应的id不对";
    private final static int NOT_MODIFIED = 304;
    private final static String CHARSET_NAME = "utf-8";

    public ReleaseListener(String serverIp, String appId) throws Exception {
        this.serverIp = serverIp;
        this.appId = appId;
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            this.appIp = address.getHostAddress();
            this.appName = address.getHostName();
        } catch (UnknownHostException e) {
            log.error("获取本机信息出错", e);
        }
        this.configUrl = "http://" + this.serverIp + "/services/config?appId=" + this.appId + "&appName=" + this
                .appName;
        this.registerInsertAppUrl = "http://" + this.serverIp + "/app/insert?id=" + this.appId + "&ip=" + this.appIp
                                    + "&hostname=" + this.appName;
        this.registerUpdateAppUrl = "http://" + this.serverIp + "/app/update?id=" + this.appId + "&ip=" + this.appIp
                                    + "&hostname=" + this.appName;
        registerApp();
    }

    public String request() throws Exception {
        String configStr;
        try {
            String requestUrl = this.configUrl;
            if (this.notificationId != null && this.notificationId.length() > 0) {
                requestUrl += "&notificationId=" + this.notificationId;
            }
            configStr = loadJSON(requestUrl);
            if (configStr.startsWith(NOT_MODIFIED + "")) {
                configStr = configStr.substring(3, configStr.length());
            }
            JSONObject json = JSONObject.parseObject(configStr);
            if (json != null) {
                Set<String> keySet = json.keySet();
                for (String key : keySet) {
                    this.notificationId = key;
                }
            }
            if (configStr.startsWith(NOT_MODIFIED + "")) {
                return NOT_MODIFIED + "";
            }
        } catch (SocketException e) {
            log.error(REQUEST_CONFIG_ERROR, e);
            throw new Exception(REQUEST_CONFIG_ERROR, e);
        } catch (Exception e) {
            log.error(REQUEST_CONFIG_ERROR, e);
            throw new Exception(REQUEST_CONFIG_ERROR, e);
        }
        return configStr;
    }

    public static String loadJSON(String configUrl) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(configUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), CHARSET_NAME));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
            if (con.getResponseCode() == NOT_MODIFIED) {
                return NOT_MODIFIED + "";
            }
        } catch (SocketException e) {
            log.error(SERVER_OFFLINE, e);
            throw new Exception(SERVER_OFFLINE, e);
        } catch (Exception e) {
            log.error("加载json数据出错", e);
            throw e;
        }
        return sb.toString();
    }

    public void registerApp() throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(this.registerInsertAppUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), CHARSET_NAME));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
            if (sb.toString().indexOf(CONFLICT_IP) != -1) {
                sb = new StringBuilder();
                url = new URL(this.registerUpdateAppUrl);
                con = (HttpURLConnection) url.openConnection();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream(), CHARSET_NAME));
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
                reader.close();
            }
        } catch (SocketException e) {
            log.error(SERVER_OFFLINE, e);
            throw new Exception(SERVER_OFFLINE, e);
        } catch (Exception e) {
            log.error(REGISTER_ERROR, e);
            throw new Exception(REGISTER_ERROR, e);
        }
        if (sb.toString().indexOf(CONFLICT_IP) != -1) {
            log.error(ID_CONFLICT_IP);
            throw new Exception(ID_CONFLICT_IP);
        }
    }

}
