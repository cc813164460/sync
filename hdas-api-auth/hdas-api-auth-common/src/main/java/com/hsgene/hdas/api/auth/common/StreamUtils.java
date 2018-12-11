package com.hsgene.hdas.api.auth.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @description: 流处理类
 * @projectName: hdas-api-auth
 * @package: com.hsgene.hdas.api.auth.util
 * @author: maodi
 * @createDate: 2018/9/20 10:57
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class StreamUtils {

    /**
     * @param is 输入流
     * @return byte[]
     * @description 将InputStream流处理为字节数组
     * @author maodi
     * @createDate 2018/9/21 11:38
     */
    public static byte[] getByteByStream(InputStream is) throws Exception {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.flush();
        return bos.toByteArray();
    }

}
