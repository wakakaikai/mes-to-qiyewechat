package com.kemflo.remote;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;

@Component
public interface WechatNoticeClient {
    @Post(url = "${baseUrl}", contentType = "application/x-www-form-urlencoded")
    Map getWechatToken(@Var("baseUrl") String baseUrl);

    @Post(url = "${url}", headers = {"Accept-Charset: utf-8", "Content-Type: application/json"}, dataType = "json")
    void sendRobotMsg(@Var("url") String url, @JSONBody Map<String, Object> body);

    @Post(url = "${url}", headers = {"Accept-Charset: utf-8", "Content-Type: application/json"}, dataType = "json")
    Map sendMsg(@Var("url") String url, @JSONBody String body);
}
