package com.kemflo.service.impl;

import static com.kemflo.common.WechatConstant.*;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dtflys.forest.exceptions.ForestNetworkException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kemflo.common.WechatConstant;
import com.kemflo.config.WxCpProperties;
import com.kemflo.model.TextCardMessage;
import com.kemflo.model.TextMessage;
import com.kemflo.model.WxText;
import com.kemflo.model.WxTextCard;
import com.kemflo.remote.WechatNoticeClient;
import com.kemflo.service.WeiXinService;

import lombok.extern.slf4j.Slf4j;

/**
 * 邮件发送信息
 *
 * @author Kevin
 * @date 2022-06-09
 */
@Service
@Slf4j
public class WeiXinServiceImpl implements WeiXinService {

    @Autowired
    private WxCpProperties wxCpProperties;
    private VelocityEngine velocityEngine;
    @Autowired
    private WechatNoticeClient wechatNoticeClient;

    private String getWechatToken() {
        String requestUrl = MessageFormat.format(wxCpProperties.getTokenUrl(), wxCpProperties.getCorpId(),
            wxCpProperties.getCorpSecret());
        try {
            Map<String, Object> map = wechatNoticeClient.getWechatToken(requestUrl);
            if (map.containsKey(ERR_CODE)
                && Integer.parseInt(map.get(ERR_CODE).toString()) == WechatConstant.ERR_CODE_SUCCESS) {
                return map.get(ACCESS_TOKEN).toString();
            }
        } catch (ForestNetworkException ex) {
            log.error("获取token失败{}", ex.getMessage());
        }
        return StringUtils.EMPTY;
    }

    /**
     * 创建一个线程池去定时更新缓存中token
     */
    ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
        (new ThreadFactoryBuilder()).setNameFormat("WechatToken-pool-%d").build());

    /**
     * 设置更新时间, 定时去更新缓存中的数据
     */
    LoadingCache<String, String> wechatTokenCache = CacheBuilder.newBuilder()
        .refreshAfterWrite(WECHAT_TOKEN_TTL, TimeUnit.SECONDS).build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                return getWechatToken();
            }

            // 异步加载缓存
            @Override
            public ListenableFuture<String> reload(String key, String oldValue) {
                // 定义任务。
                ListenableFutureTask<String> futureTask = ListenableFutureTask.create(() -> getWechatToken());
                // 异步执行任务
                executor.execute(futureTask);
                return futureTask;
            }
        });

    @PostConstruct
    public void initVelocityEngine() {
        velocityEngine = new VelocityEngine();
        Properties p = new Properties();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        // 配置资源
        // velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, "/usr/local/templates/");
        velocityEngine.init(p);
    }

    @Override
    public void sendSimpleText(String[] toArr, String content) {
        if (ArrayUtils.isEmpty(toArr)) {
            return;
        }
        // 1. 获取 token
        String accessToken = wechatTokenCache.getIfPresent(ACCESS_TOKEN);
        if (StringUtils.isBlank(accessToken)) {
            log.error("发送文本消息获取token失败");
            return;
        }
        // 2 构建普通文本对象
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        TextMessage message = new TextMessage();
        // 1.1非必需
        // 不区分大小写
        message.setTouser(StringUtils.join(toArr, "|"));
        // 1.2必需
        message.setMsgtype("text");
        message.setAgentid(wxCpProperties.getAgentId());
        WxText wxText = new WxText();
        wxText.setContent(content);
        message.setText(wxText);
        String jsonMessage = gson.toJson(message);
        // 3. 发送 json 形式的获取，获取响应信息
        messageResponse(accessToken, jsonMessage);
    }

    private String getVelocityMailText(Map<String, Object> dataMap, String templateName) {
        VelocityContext velocityContext = new VelocityContext(dataMap);
        StringWriter writer = new StringWriter();
        velocityEngine.mergeTemplate(templateName, "UTF-8", velocityContext, writer);
        return writer.toString();
    }

    @Override
    public void sendVelocityText(String title, String[] toArr, Map<String, Object> dataMap, String templateName) {
        if (ArrayUtils.isEmpty(toArr)) {
            return;
        }
        // 1.获取access_token:根据企业id和应用密钥获取access_token,并拼接请求url
        String accessToken = wechatTokenCache.getIfPresent(ACCESS_TOKEN);
        if (StringUtils.isBlank(accessToken)) {
            log.error("发送文本消息获取token失败");
            return;
        }
        // 2.获取发送对象，并转成json
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        TextCardMessage textCardMessage = new TextCardMessage();
        // 1.1非必需
        // 不区分大小写
        textCardMessage.setTouser(StringUtils.join(toArr, "|"));
        // 1.2必需
        textCardMessage.setMsgtype("textcard");
        textCardMessage.setAgentid(wxCpProperties.getAgentId());
        WxTextCard wxTextCard = new WxTextCard();
        wxTextCard.setTitle(title);
        wxTextCard.setDescription(getVelocityMailText(dataMap, templateName));
        wxTextCard.setUrl("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        textCardMessage.setTextcard(wxTextCard);
        String jsonMessage = gson.toJson(textCardMessage);
        // 3.获取请求的url
        messageResponse(accessToken, jsonMessage);
    }

    /**
     * 将消息通过企业微信发送给相应的用户
     *
     * @param accessToken token信息
     * @param jsonMessage 发送的消息
     */
    public void messageResponse(String accessToken, String jsonMessage) {
        String url = MessageFormat.format(wxCpProperties.getSendMsgUrl(), accessToken);
        // 4.调用接口，发送消息
        Map map = wechatNoticeClient.sendMsg(url, jsonMessage);
        // 5.消息成功处理
        if (map.containsKey(ERR_CODE) && ERR_CODE_SUCCESS == Integer.parseInt(map.get(ERR_CODE).toString())) {
            log.info("消息发送成功{}", jsonMessage);
            return;
        }
        log.info("消息发送失败{}", map);
    }
}
