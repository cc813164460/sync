package com.hsgene.elasticsearch.sync.domain;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 日期格式化
 * @projectName: sync_elasticsearch
 * @package: sync.domain
 * @author: maodi
 * @createDate: 2018/12/4 12:01
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */

public class DateJsonDeserializer extends JsonDeserializer<Date> {

    private final static Logger LOGGER = LogManager.getLogger(DateJsonDeserializer.class);

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, DeserializationContext
            deserializationContext) throws IOException {
        try {
            if (jsonParser != null && StringUtils.isNotEmpty(jsonParser.getText())) {
                return format.parse(jsonParser.getText());
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}