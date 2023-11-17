package com.kemflo.remote;


import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnSuccess;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * interface 上挂了 @Success 注解
 * <p>该接口类下的所有方法都依据
 * <p>自定义的 MySuccessCondition1 请求成功条件类
 * <p>为请求是否成功的判定条件
 * <p>除非该方法定义了自己的请求成功条件
 */
@Service
public interface MesClient {
    @Post(url = "${baseUrl}/cloudt/authorization/oauth2/token",
            contentType = "x-www-form-urlencoded"
           )
    void getMesToken(@Var("baseUrl") String baseUrl, @JSONBody JSONObject body, OnSuccess<String> onSuccess, OnError onError);

    @Post(url = "${baseUrl}/mng/demo/page",
            async = false,
            headers = {
                    "Accept-Charset: utf-8",
                    "Content-Type: application/json"
            })
    @Headers({"Authorization: ${authorization}"})
    void asyncSendMes(@Var("baseUrl") String baseUrl, @Var("authorization") String authorization, @JSONBody Map<String, Object> body, OnSuccess<String> onSuccess, OnError onError);

}
