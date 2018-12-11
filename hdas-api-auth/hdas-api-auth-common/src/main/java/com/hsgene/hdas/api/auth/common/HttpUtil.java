package com.hsgene.hdas.api.auth.common;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @description:
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.common
 * @author: maodi
 * @createDate: 2018/10/8 17:53
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
@Slf4j
public class HttpUtil {

    public static String getValueByHttpGet(String hUrl) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(hUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            //只有https才需要加安全验证，http直接跳过
            if (hUrl.startsWith("https")) {
                //此处写了个假的安全验证，以便请求https能够成功
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
                ((HttpsURLConnection) con).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection) con).setHostnameVerifier(new TrustAnyHostnameVerifier());
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
            reader.close();
        } catch (Exception e) {
            log.error("请求http时", e);
        }
        return sb.toString();
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
