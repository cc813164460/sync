package com.hsgene.hdas.cmcs.modules.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @description: 加密工具类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.common.util
 * @author: maodi
 * @createDate: 2018/5/29 16:00
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class EncrypAESUtil {

    /**
     * 密钥
     */
    private static final String aesKey = "C5C01A392302320A";

    /**
     * 加密
     *
     * @param b
     * @return
     */
    public static byte[] encryptAES(byte[] b) {
        return cryptAES(b, Cipher.ENCRYPT_MODE);
    }

    private static byte[] cryptAES(byte[] b, int mode) {
        if (aesKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        //判断Key是否为16位
        if (aesKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        try {
            byte[] raw = aesKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, skeySpec);
            byte[] encrypted = cipher.doFinal(b);
            return encrypted;
        } catch (Exception e) {
            if (mode == Cipher.ENCRYPT_MODE) {
                System.out.println("数据加密时发生异常...");
            } else if (mode == Cipher.DECRYPT_MODE) {
                System.out.println("数据解密时发生异常...");
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param b
     * @return 由于C语言在加密时采用了模式，所以JAVA在解析时需要采用模式来解密
     */
    public static byte[] decryptAES(byte[] b) {
        return cryptAES(b, Cipher.DECRYPT_MODE);
    }

    /**
     * @return java.lang.String
     * @throws
     * @param: str 字符串
     * @description: 字符串加密为字符串
     * @Date: 14:12 2017/9/29
     */
    public static String encodeTostring(String str) {
        return new BASE64Encoder().encode(encryptAES(str.getBytes()));
    }

    public static String decryptTostring(byte[] b) {
        return new String(decryptAES(b));
    }

    /**
     * @return java.lang.String
     * @throws
     * @param: str 字符串
     * @description: 字符串解密为字符串
     * @Date: 14:12 2017/9/29
     */
    public static String decryptTostring(String str) {
        try {
            return decryptTostring(new BASE64Decoder().decodeBuffer(str));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            File file = new File("./encode.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            int length = args.length;
            int count = 0;
            for (String arg : args) {
                String encode = EncrypAESUtil.encodeTostring(arg);
                String outEncode = arg + "--->" + encode;
                System.out.println(outEncode);
                fileWriter.write(outEncode);
                if (++count != length) {
                    fileWriter.write("\r\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
