package com.kemflo.remote;


import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.annotation.Var;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface WechatNoticeClient {


    @Post(
            url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key={key}",
            headers = {
                    "Accept-Charset: utf-8",
                    "Content-Type: application/json"
            },
            sslProtocol = "TLS",
            dataType = "json")
    void sendWechatMsg(@Var("key") String key, @JSONBody Map<String, Object> body);
}
