package com.kemflo.remote;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;

@Service
public interface MesClient {
    @Post(url = "${baseUrl}", contentType = "application/x-www-form-urlencoded")
    Map getMesToken(@Var("baseUrl") String baseUrl, @Body("grant_type") String grantType,
        @Body("client_id") String clientId, @Body("client_secret") String clientSecret);

    @Post(url = "${baseUrl}", headers = {"Accept-Charset: utf-8", "Content-Type: application/json"})
    @Headers({"Authorization: ${authorization}"})
    void asyncSendMes(@Var("baseUrl") String baseUrl, @Var("authorization") String authorization,
        @JSONBody Map<String, Object> body, OnSuccess<String> onSuccess, OnError onError);

}
