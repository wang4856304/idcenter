package com.wj.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * @author jun.wang
 * @title: BaseController
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/9/5 11:44
 */
public class BaseController {

    private Logger logger = LoggerFactory.getLogger(BaseController.class);

    private HttpHeaders buildHeaders(String code, String msg) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("code", code);
        httpHeaders.set("msg", msg);
        return httpHeaders;
    }

    public<T>  ResponseEntity<T> buildSuccessResponse(T t) {
        HttpHeaders httpHeaders = buildHeaders("0", "success");
        logger.info("response header={}, data={}", JSONObject.toJSONString(httpHeaders), JSONObject.toJSONString(t));
        return ResponseEntity.ok().headers(httpHeaders).body(t);
    }

    public<T>  ResponseEntity<T> buildResponse(String code, String msg) {
        HttpHeaders httpHeaders = buildHeaders(code, msg);
        logger.info("response header={}", JSONObject.toJSONString(httpHeaders));
        return ResponseEntity.ok().headers(buildHeaders(code, msg)).build();
    }
}
