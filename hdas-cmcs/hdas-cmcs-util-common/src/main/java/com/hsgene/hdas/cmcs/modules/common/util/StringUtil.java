package com.hsgene.hdas.cmcs.modules.common.util;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.*;
import info.monitorenter.cpdetector.io.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 字符串相关工具类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.common.util
 * @author: maodi
 * @createDate: 2018/6/11 15:37
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class StringUtil {

    /**
     * @param
     * @return long
     * @description 获取id
     * @author maodi
     * @createDate 2018/6/13 17:19
     */
    public static long getId() {
        return System.currentTimeMillis() * 1000;
    }

    public static String getMainVersionNum(long num) {
        return "Z" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getMainFrontVersion() {
        return "Z";
    }

    public static String getGrayVersionNum(long num) {
        return "HD" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getGrayFrontVersion() {
        return "HD";
    }

    public static String getMainReleasedVersionNum(long num) {
        return "ZZS" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getMainReleasedFrontVersion() {
        return "ZZS";
    }

    public static String getGrayReleasedVersionNum(long num) {
        return "HDZS" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getGrayReleasedFrontVersion() {
        return "HDZS";
    }

    public static String getPublicMainVersionNum(long num) {
        return "GGZ" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getPublicMainFrontVersion() {
        return "GGZ";
    }

    public static String getPublicGrayVersionNum(long num) {
        return "GGHD" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getPublicGrayFrontVersion() {
        return "GGHD";
    }

    public static String getPublicMainReleasedVersionNum(long num) {
        return "GGZZS" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getPublicMainReleasedFrontVersion() {
        return "GGZZS";
    }

    public static String getPublicGrayReleasedVersionNum(long num) {
        return "GGHDZS" + (System.currentTimeMillis() * 1000 + num);
    }

    public static String getPublicGrayReleasedFrontVersion() {
        return "GGHDZS";
    }

    /**
     * @description 获取当前timestamp，去掉毫秒
     * @author maodi  
     * @createDate 2018/9/7 14:02  
     * @param   
     * @return java.sql.Timestamp  
     */  
    public static Timestamp getNowTimestamp() {
        long time = (System.currentTimeMillis() / 1000) * 1000;
        return new Timestamp(time);
    }

    /**
     * @param
     * @return java.lang.String 当前时间戳格式化过后
     * @description 将当前时间戳格式化为yyyy-MM-dd HH:mm:ss
     * @author maodi
     * @createDate 2018/9/7 14:01
     */
    public static String getFormatTime() {
        long time = (System.currentTimeMillis() / 1000) * 1000;
        return DateUtil.timestamp2Date(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @param ids        条目id数组
     * @param userId     用户id
     * @param versionNum 版本号
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @description 操作版本号前面字符的map
     * @author maodi
     * @createDate 2018/9/7 13:59
     */
    public static Map<String, Object> actionMapFrontVersion(long[] ids, long userId, String versionNum) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("ids", ids);
        map.put("userId", userId);
        Timestamp timestamp = StringUtil.getNowTimestamp();
        map.put("lastUpdateTime", timestamp);
        map.put("updateTime", timestamp);
        map.put("frontVersion", versionNum);
        return map;
    }

    /**
     * @param appId 实例的id
     * @param time  时间
     * @return java.lang.String 发布的标识id
     * @description 根据实例的id和时间获取发布的标识id
     * @author maodi
     * @createDate 2018/9/7 13:58
     */
    public static String getNotificationId(long appId, long time) {
        return appId + "_" + time;
    }

    /**
     * @param str json文件内容字符串
     * @return boolean 是否是json文件格式
     * @description 验证json文件格式
     * @author maodi
     * @createDate 2018/9/7 13:58
     */
    public static boolean validJSONFile(String str) {
        try {
            JSONObject.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param str json字符串
     * @return java.lang.String 格式化过后的json字符串
     * @description 美化（格式化）json格式
     * @author maodi
     * @createDate 2018/9/7 13:57
     */
    public static String formatJSONFile(String str) {
        try {
            Gson gs = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(str);
            return gs.toJson(je);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * @param str xml文件内容字符串
     * @return boolean  是否是xml文件格式
     * @description 验证xml文件格式
     * @author maodi
     * @createDate 2018/9/7 13:56
     */
    public static boolean validXmlFile(String str) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(str.getBytes()));
            doc.getRootElement();
        } catch (DocumentException e) {
            return false;
        }
        return true;
    }

    /**
     * @param str yml文件内容字符串
     * @return boolean  验证yml文件格式
     * @description 是否是yml文件格式
     * @author maodi
     * @createDate 2018/9/7 13:56
     */
    public static boolean validYmlFile(String str) {
        try {
            Yaml yaml = new Yaml();
            yaml.load(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * @param isReleased 发布状态
     * @param versionId  主灰版本
     * @param isPublic   是否公开
     * @param num        数字
     * @return java.lang.String   版本号
     * @description 根据发布状态、主灰版本、是否公开、数字获取版本号
     * @author maodi
     * @createDate 2018/9/7 13:55
     */
    public static String getVersionNum(long isReleased, long versionId, long isPublic, long num) {
        String versionNum;
        if (isReleased == 0) {
            if (versionId == 0) {
                if (isPublic == 0) {
                    versionNum = StringUtil.getMainVersionNum(num);
                } else {
                    versionNum = StringUtil.getPublicMainVersionNum(num);
                }
            } else {
                if (isPublic == 0) {
                    versionNum = StringUtil.getGrayVersionNum(num);
                } else {
                    versionNum = StringUtil.getPublicGrayVersionNum(num);
                }
            }
        } else {
            if (versionId == 0) {
                if (isPublic == 0) {
                    versionNum = StringUtil.getMainReleasedVersionNum(num);
                } else {
                    versionNum = StringUtil.getPublicMainReleasedVersionNum(num);
                }
            } else {
                if (isPublic == 0) {
                    versionNum = StringUtil.getGrayReleasedVersionNum(num);
                } else {
                    versionNum = StringUtil.getPublicGrayReleasedVersionNum(num);
                }
            }
        }
        return versionNum;
    }

    /**
     * @param isReleased 发布状态
     * @param versionId  主灰版本
     * @param isPublic   是否公开
     * @return java.lang.String  版本号前面字符
     * @description 根据发布状态、主灰版本、是否公开获取版本号前面字母
     * @author maodi
     * @createDate 2018/9/7 13:53
     */
    public static String getFrontVersion(long isReleased, long versionId, long isPublic) {
        String frontVersion;
        if (isReleased == 0) {
            if (versionId == 0) {
                if (isPublic == 0) {
                    frontVersion = StringUtil.getMainFrontVersion();
                } else {
                    frontVersion = StringUtil.getPublicMainFrontVersion();
                }
            } else {
                if (isPublic == 0) {
                    frontVersion = StringUtil.getGrayFrontVersion();
                } else {
                    frontVersion = StringUtil.getPublicGrayFrontVersion();
                }
            }
        } else {
            if (versionId == 0) {
                if (isPublic == 0) {
                    frontVersion = StringUtil.getMainReleasedFrontVersion();
                } else {
                    frontVersion = StringUtil.getPublicMainReleasedFrontVersion();
                }
            } else {
                if (isPublic == 0) {
                    frontVersion = StringUtil.getGrayReleasedFrontVersion();
                } else {
                    frontVersion = StringUtil.getPublicGrayReleasedFrontVersion();
                }
            }
        }
        return frontVersion;
    }

    /**
     * @param arrays long[]
     * @return java.util.List<java.lang.Object> List<Long>
     * @description 将long[]转化为List<Long>
     * @author maodi
     * @createDate 2018/9/7 13:52
     */
    public static List<Long> longArrayAsList(long[] arrays) {
        List<Long> list = new ArrayList<>();
        for (long array : arrays) {
            list.add(array);
        }
        return list;
    }

    /**
     * @param arrays long[]
     * @return java.util.Set<java.lang.Long> Set<Long>
     * @description 将long[]转化为Set<Long>
     * @author maodi
     * @createDate 2018/9/7 13:52
     */
    public static Set<Long> longArrayAsSet(long[] arrays) {
        Set<Long> set = new HashSet<>();
        for (long array : arrays) {
            set.add(array);
        }
        return set;
    }

    /**
     * @param set Set<Long>
     * @return long[]  long[]
     * @description 将Set<Long>转化为long[]
     * @author maodi
     * @createDate 2018/9/7 13:51
     */
    public static long[] longSetAsArray(Set<Long> set) {
        int size = set.size();
        long[] arrays = new long[size];
        int i = 0;
        for (long l : set) {
            arrays[i++] = l;
        }
        return arrays;
    }

    /**
     * @param arrays long[]
     * @return java.lang.String[]  String[]
     * @description 将long[]转化为String[]
     * @author maodi
     * @createDate 2018/9/7 13:50
     */
    public static String[] longArrayAsStringArray(long[] arrays) {
        int length = arrays.length;
        String[] strs = new String[length];
        for (int i = 0; i < length; i++) {
            strs[i] = String.valueOf(arrays[i]);
        }
        return strs;
    }

    /**
     * @param in 输入流
     * @return java.lang.String 编码格式
     * @description 获取文件编码（没有判断是否有BOM）
     * @author maodi
     * @createDate 2018/9/7 13:47
     */
    public static String getEncode(InputStream in) {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(new ParsingDetector(false));
        detector.add(new ByteOrderMarkDetector());
        detector.add(JChardetFacade.getInstance());
        // ASCIIDetector用于ASCII编码测定
        detector.add(ASCIIDetector.getInstance());
        // UnicodeDetector用于Unicode家族编码的测定
        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        try {
            charset = detector.detectCodepage(in, in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (charset != null) {
            String encoding = charset.name();
            if (!encoding.startsWith("UTF-8") && !encoding.startsWith("utf-8")) {
                encoding = "gbk";
            }
            return encoding;
        }
        return null;
    }

    /**
     * 中文正则
     */
    private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";
    /**
     * 韩文正则1
     */
    private static String REGEX_KOREAN1 = "[/x3130-/x318F]";
    /**
     * 韩文正则2
     */
    private static String REGEX_KOREAN2 = "[/xAC00-/xD7A3]";
    /**
     * 日文正则
     */
    private static String REGEX_JAPANESE = "[\u0800-\u4e00]";

    /**
     * @param str 字符串
     * @return java.lang.String 过滤掉中文的字符串
     * @description 过滤掉中文
     * @author maodi
     * @createDate 2018/8/15 12:02
     */
    public static String getNoOtherLanguage(String str) {
        Pattern pat = Pattern.compile(REGEX_CHINESE);
        Matcher mat = pat.matcher(str);
        return mat.replaceAll("");
    }

    /**
     * @param str 字符串
     * @return java.lang.String 去掉开头数字的字符串
     * @description 去掉以数字开头字符串开头的数字
     * @author maodi
     * @createDate 2018/9/7 13:44
     */
    public static String getNoStartNumStr(String str) {
        return str.replaceFirst("\\d+", "");
    }

    /**
     * @param in 输入流
     * @return java.lang.String 文件的编码格式（包含是否BOM）
     * @description 获取文件的编码格式（包含是否BOM）
     * @author maodi
     * @createDate 2018/9/7 13:43
     */
    public static String getFileEncode(InputStream in) throws IOException {
        String dc = Charset.defaultCharset().name();
        UnicodeInputStream uin = new UnicodeInputStream(in, dc);
        String encode = uin.getEncoding();
        uin.close();
        byte[] head = new byte[3];
        in.read(head);
        boolean bomFlag = head[0] == -17 && head[1] == -69 && head[2] == -65;
        String code = "GBK";
        if (head[0] == -1 && head[1] == -2) {
            code = "UTF-16";
        }
        if (head[0] == -2 && head[1] == -1) {
            code = "Unicode";
        }
        //UTF-8不带BOM
        if ("UTF-8".equals(encode) && !bomFlag) {
            code = "UTF-8";
        }
        //带BOM
        if (bomFlag) {
            code = "UTF-8 BOM";
        }
        return code;
    }

    public static List<String> getParameterList(String value) {
        String[] strs = value.split("【");
        List<String> list = new ArrayList<>();
        for (int i = 0, length = strs.length; i < length; i++) {
            String s = strs[i];
            if (s.indexOf("】") != -1) {
                String[] strss = strs[i].split("】");
                if (strss.length > 0) {
                    list.add(strss[0]);
                }
            }
        }
        return list;
    }

}