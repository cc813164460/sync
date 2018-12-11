package com.hsgene.hdas.cmcs.modules.admin.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @projectName: hdas-cmcs
 * @package: com.hsgene.hdas.cmcs.modules.admin.util
 * @author: maodi
 * @createDate: 2018/7/13 13:152
 * @version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class DeferredResultWrapper {

    /**
     * 2 seconds
     */
    public static final long TIMEOUT = 2000;
    private static final ResponseEntity<ConcurrentHashMap> NOT_MODIFIED_RESPONSE_JSON = new
            ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    private DeferredResult<ResponseEntity<ConcurrentHashMap>> result;

    public DeferredResultWrapper() {
        result = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE_JSON);
    }

    public void onTimeout(Runnable timeoutCallback) {
        result.onTimeout(timeoutCallback);
    }

    public void onCompletion(Runnable completionCallback) {
        result.onCompletion(completionCallback);
    }

    public void setResult(ConcurrentHashMap notifications, HttpStatus status) {
        result.setResult(new ResponseEntity<>(notifications, status));
    }

    public DeferredResult<ResponseEntity<ConcurrentHashMap>> getResult() {
        return result;
    }
    
}
