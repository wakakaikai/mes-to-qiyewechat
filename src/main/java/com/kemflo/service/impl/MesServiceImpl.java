package com.kemflo.service.impl;

import static com.kemflo.common.WechatConstant.AUTHORIZATION;
import static com.kemflo.common.WechatConstant.MES_TOKEN_TTL;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.kemflo.common.WechatConstant;
import com.kemflo.remote.MesClient;
import com.kemflo.service.MesService;
import com.kemflo.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MesServiceImpl implements MesService {
    @Value("${mes.baseUrl}")
    private String baseUrl;
    @Value("${mes.authUrl}")
    private String authUrl;

    @Value("${mes.client_id}")
    private String clientId;

    @Value("${mes.client_secret}")
    private String clientSecret;
    @Autowired
    private MesClient mesClient;
    @Autowired
    private NoticeService noticeService;

    /**
     * 创建一个线程池去定时更新缓存中token
     */
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

    /**
     * 设置更新时间, 定时去更新缓存中的数据
     */
    LoadingCache<String, String> mesTokenCache = CacheBuilder.newBuilder()
        .refreshAfterWrite(MES_TOKEN_TTL, TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                String mesToken = getMesToken();
                mesTokenCache.put(AUTHORIZATION, mesToken);
                return mesToken;
            }

            // 异步加载缓存
            @Override
            public ListenableFuture<String> reload(String key, String oldValue) {
                // 定义任务。
                ListenableFutureTask<String> futureTask = ListenableFutureTask.create(() -> {
                    String mesToken = getMesToken();
                    mesTokenCache.put(AUTHORIZATION, mesToken);
                    return mesToken;
                });
                // 异步执行任务
                executor.execute(futureTask);
                return futureTask;
            }
        });

    private String getMesToken() {
        try {
            Map<String, Object> map = mesClient.getMesToken(authUrl, WechatConstant.GRANT_TYPE, clientId, clientSecret);
            if (map.containsKey(WechatConstant.ACCESS_TOKEN)) {
                return WechatConstant.TOKEN_TYPE + " " + map.get(WechatConstant.ACCESS_TOKEN).toString();
            }
        } catch (ForestNetworkException ex) {
            log.error("获取MesToken失败{}", ex.getMessage());
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String sendMesSystem(String systemName) {
        switch (systemName) {
            case "yst-mes":
                return getMesInfo("/yst/mes-service");
            case "yst-mes-scan":
                return getMesInfo("/yst/mes-scan");
            case "yst-mes-report":
                return getMesInfo("/yst/mes-report");
            case "yst-mes-ws":
                break;
            default:
                log.error("系统名称异常{}", systemName);
        }
        return null;
    }

    private String getMesInfo(String prefix) {
        AtomicReference<String> resultMsg = new AtomicReference<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        JSONObject keyString = new JSONObject();
        keyString.put("code", "001");
        // 异步执行
        String requestUrl = baseUrl + prefix + "/mng/demo/page";
        String token = StringUtils.EMPTY;
        try {
            token = mesTokenCache.get(AUTHORIZATION);
        } catch (ExecutionException e) {
            log.error("服务{}获取token调用失败{}", prefix, e);
        }
        if (StringUtils.isBlank(token)) {
            return resultMsg.get();
        }
        mesClient.asyncSendMes(requestUrl, token, keyString, (result, req, res) -> {
            resultMsg.set(result);
            // 请求成功，处理响应结果
            JSONObject object = JSON.parseObject(result);
            Integer code = object.getInteger("code");
            if (code.equals(HttpStatus.OK.value())) {
                log.info("服务{}调用成功{}", prefix, result);
            } else {
                noticeService.sendImageAndTxtMsg(res.getContent());
                log.error("服务{}调用失败{}", prefix, result);
            }
        }, (ex, request, response) -> {
            resultMsg.set(ex.getMessage());
            noticeService.sendImageAndTxtMsg(ex.getMessage());
            log.error("服务{}调用失败{}", prefix, ex.getMessage());
        });
        return resultMsg.get();
    }
}
