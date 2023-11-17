package com.kemflo.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kemflo.common.WxConstants;
import com.kemflo.remote.MesClient;
import com.kemflo.utils.MyNoticeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class TestController {
    @Autowired
    private MyNoticeUtil myNoticeUtil;

    @Autowired
    private MesClient mesClient;


    @Value("${thirdApi.baseUrl}")
    private String baseUrl;

    @GetMapping("/doTest")
    public String doTest(@RequestParam("testType") String testType) {
        if (testType.equals("1")) {
            myNoticeUtil.sendTextMsg();
        }
        if (testType.equals("2")) {
            myNoticeUtil.sendMarkDownTextMsg();
        }
        if (testType.equals("3")) {
            myNoticeUtil.sendImageMsg();
        }
        if (testType.equals("4")) {
            myNoticeUtil.sendImageAndTxtMsg("");
        }
        return "success";
    }

    @GetMapping("/test")
    public void test() {

        JSONObject keyString = new JSONObject();
        keyString.put("code", "001");

        // 异步执行
        mesClient.asyncSendMes(baseUrl, WxConstants.accessToken, keyString, (result, req, res) -> {
            // 请求成功，处理响应结果
            JSONObject object = JSON.parseObject(result);
            Integer code = object.getInteger("code");
            String records = object.getString("records");
            if (code.equals(HttpStatus.OK.value())) {
                log.info("成功响应={}", records);
            } else {
                myNoticeUtil.sendImageAndTxtMsg(res.getContent());
            }
        }, (ex, request, response) -> {
            myNoticeUtil.sendImageAndTxtMsg(ex.getMessage());
        });

    }
}
