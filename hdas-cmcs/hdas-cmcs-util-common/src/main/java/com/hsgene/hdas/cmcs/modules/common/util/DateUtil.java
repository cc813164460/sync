package com.hsgene.hdas.cmcs.modules.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @description: 日期工具类
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.common.util
 * @author: maodi
 * @createDate: 2018/5/29 16:00
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class DateUtil {

    /**
     * @param timestamp unix时间戳
     * @param formats   时间格式
     * @return java.lang.String
     * @description 根据unix时间戳和时间格式返回时间
     * @author maodi
     * @createDate 2018/6/13 17:06
     */
    public static String timestamp2Date(long timestamp, String formats) {
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }

}
