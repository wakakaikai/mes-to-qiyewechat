package com.kemflo.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.http.ForestResponse;
import com.kemflo.common.WxConstants;
import com.kemflo.remote.MesClient;
import com.kemflo.service.InitService;
import com.kemflo.utils.MyNoticeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class InitServiceImpl implements InitService {
    @Value("${thirdApi.authUrl}")
    private String authUrl;
    @Autowired
    private MesClient mesClient;
    @Autowired
    private MyNoticeUtil myNoticeUtil;

    @Override
    public void init() {
        JSONObject keyString = new JSONObject();
        keyString.put("grant_type", "client_credentials");
        keyString.put("client_id", "arch_dev");
        keyString.put("client_secret", "123456");
        try {
            mesClient.getMesToken(authUrl, keyString, (result, req, res) -> {
                // 请求成功，处理响应结果
                JSONObject object = JSON.parseObject(result);
                Integer code = object.getInteger("code");
                if (code.equals(HttpStatus.OK.value())) {
                    WxConstants.accessToken = object.get("token_type") + " " + object.get("access_token");
                    log.info(">>> update access_token at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " --------> " + WxConstants.accessToken);
                } else {
//                    myNoticeUtil.sendImageAndTxtMsg(res.getContent());
                }
            }, (ex, request, response) -> {
//                myNoticeUtil.sendImageAndTxtMsg(ex.getMessage());
            });
        } catch (ForestNetworkException ex) {
            int status = ex.getStatusCode(); // 获取请求响应状态码
            ForestResponse response = ex.getResponse(); // 获取Response对象
            String content = response.getContent(); // 获取请求的响应内容
            Object resResult = response.getResult(); // 获取方法返回类型对应的最终数据结果
        }

    }
}
