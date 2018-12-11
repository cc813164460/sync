package com.hsgene.hdas.cmcs.modules.api;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * @description:
 * @projectName: apollo
 * @package: com.ctrip.framework.apollo.demo
 * @author: maodi
 * @createDate: 2018/7/13 17:41
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class ConfigHttp {
    private final static long SLEEP_TIME = 200;

    private final static Logger log = Logger.getLogger(ReleaseListener.class);

    private final static String CONFLICT_IP = "跟其它ip地址重复";

    public static void main(String[] args) {
        httpTest("0", "192.168.10.156", null, null);
        /*if ("1".equals(args[0])) {
            httpTest(args[1], args[2], args[3], args[4]);
        } else {
            apiTest(args[1], args[2]);
        }*/
    }

    public static void apiTest(String appId, String serverIp) {
        if (appId == null) {
            appId = "0";
        }
        if (serverIp == null) {
            serverIp = "192.168.10.156";
        }
        try {
            ReleaseListener listener = new ReleaseListener(serverIp, appId);
            int count = 0;
            while (true) {
                long startTime = System.currentTimeMillis();
                String str1 = "第" + ++count + "次请求...";
                System.out.println(str1);
                log.info(str1);
                String configStr = listener.request();
                long endTime = System.currentTimeMillis();
                if (configStr == null || configStr.length() < 1) {
                    configStr = "304";
                }
                String str2 = endTime - startTime + "--->" + configStr;
                System.out.println(str2);
                log.info(str2);
                Thread.sleep(SLEEP_TIME);
            }
        } catch (Exception e) {
            log.error("api测试出错", e);
        }
    }

    public static void httpTest(String appId, String serverIp, String appIp, String appName) {
        String notificationId = "";
        long count = 0;
        if (appId == null) {
            appId = "1";
        }
        if (serverIp == null) {
            serverIp = "192.168.10.156";
        }
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            if (appIp == null) {
                appIp = address.getHostAddress();
            }
            if (appName == null) {
                appName = address.getHostName();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String registerInsertAppUrl = "http://" + serverIp + "/app/insert?id=" + appId + "&ip=" + appIp + "&hostname="
                                      + appName;
        String registerUpdateAppUrl = "http://" + serverIp + "/app/update?id=" + appId + "&ip=" + appIp + "&hostname="
                                      + appName;
        try {
            //注册实例，需要ip和id对应，否则报错
            registerApp(registerInsertAppUrl, registerUpdateAppUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            String url = "http://" + serverIp + "/services/config?appId=" + appId + "&appIp=" + appIp + "&appName=" +
                         appName;
            if (notificationId.length() > 0) {
                url += "&notificationId=" + notificationId;
            }
            long startTime = System.currentTimeMillis();
            String str1 = "第" + ++count + "次，请求[" + url + "]...";
            System.out.println(str1);
            log.info(str1);
            String configStr = loadJSON(url);
            long endTime = System.currentTimeMillis();
            String str2 = endTime - startTime + "--->" + configStr;
            System.out.println(str2);
            log.info(str2);
            if (configStr.startsWith("304")) {
                configStr = configStr.substring(3, configStr.length());
            }
            JSONObject json = JSONObject.parseObject(configStr);
            if (json != null) {
                Set<String> keySet = json.keySet();
                for (String key : keySet) {
                    notificationId = key;
                }
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                log.error("睡眠中断出错", e);
            }
        }
    }

    public static String loadJSON(String hUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(hUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
            if (con.getResponseCode() == 304) {
                return "304" + sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("加载json出错", e);
        }
        return sb.toString();
    }

    public static void registerApp(String registerInsertAppUrl, String registerUpdateAppUrl) throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(registerInsertAppUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
            if (sb.toString().indexOf(CONFLICT_IP) != -1) {
                sb = new StringBuilder();
                url = new URL(registerUpdateAppUrl);
                con = (HttpURLConnection) url.openConnection();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                while ((inputLine = reader.readLine()) != null) {
                    sb.append(inputLine);
                }
                reader.close();
            }
        } catch (Exception e) {
            log.error("注册app出错", e);
            throw new Exception("注册app出错", e);
        }
        if (sb.toString().indexOf(CONFLICT_IP) != -1) {
            log.error("该ip已经注册，该ip对应的id不对");
            throw new Exception("该ip已经注册，该ip对应的id不对");
        }
    }
}
