package com.kemflo.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kemflo.handle.*;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.constant.WxCpConsts;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;

/**
 * @author Kevin
 */
@Component
@Slf4j
public class WxCpConfiguration {
    private LogHandler logHandler;
    private NullHandler nullHandler;
    private LocationHandler locationHandler;
    private MenuHandler menuHandler;
    private MsgHandler msgHandler;
    private UnsubscribeHandler unsubscribeHandler;
    private SubscribeHandler subscribeHandler;

    private WxCpProperties properties;

    private static Map<Integer, WxCpMessageRouter> routers = Maps.newHashMap();
    private static Map<Integer, WxCpService> cpServices = Maps.newHashMap();

    @Autowired
    public WxCpConfiguration(LogHandler logHandler, NullHandler nullHandler, LocationHandler locationHandler,
        MenuHandler menuHandler, MsgHandler msgHandler, UnsubscribeHandler unsubscribeHandler,
        SubscribeHandler subscribeHandler, WxCpProperties properties) {
        this.logHandler = logHandler;
        this.nullHandler = nullHandler;
        this.locationHandler = locationHandler;
        this.menuHandler = menuHandler;
        this.msgHandler = msgHandler;
        this.unsubscribeHandler = unsubscribeHandler;
        this.subscribeHandler = subscribeHandler;
        this.properties = properties;
    }

    public static Map<Integer, WxCpMessageRouter> getRouters() {
        return routers;
    }

    @Bean
    public WxCpService wxCpService() {
        WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
        configStorage.setCorpId(this.properties.getCorpId());
        configStorage.setCorpSecret(this.properties.getCorpSecret());
        configStorage.setAgentId(this.properties.getAgentId());
        configStorage.setToken(this.properties.getToken());
        configStorage.setAesKey(this.properties.getAesKey());
        WxCpService wxCpService = new WxCpServiceImpl();
        wxCpService.setWxCpConfigStorage(configStorage);
        log.info("初始化微信配置{}", configStorage);

        cpServices.put(this.properties.getAgentId(), wxCpService);
        getCpService(this.properties.getAgentId(), this.properties.getCorpSecret(), this.properties.getCorpId());
        return wxCpService;
    }

    public WxCpService getCpService(Integer agentId, String secret, String corpId) {
        AppConfig appConfig = AppConfig.builder().agentId(agentId).secret(secret).build();

        List<AppConfig> list = Lists.newArrayList();
        list.add(appConfig);
        properties.setAppConfigs(list);
        properties.setCorpId(corpId);

        cpServices = this.properties.getAppConfigs().stream().map(a -> {
            WxCpDefaultConfigImpl configStorage = new WxCpDefaultConfigImpl();
            configStorage.setCorpId(this.properties.getCorpId());
            configStorage.setAgentId(a.getAgentId());
            configStorage.setCorpSecret(a.getSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getAesKey());
            WxCpServiceImpl service = new WxCpServiceImpl();
            service.setWxCpConfigStorage(configStorage);
            routers.put(a.getAgentId(), this.newRouter(service));
            return service;
        }).collect(Collectors.toMap(service -> service.getWxCpConfigStorage().getAgentId(), a -> a));
        return cpServices.get(agentId);
    }

    private WxCpMessageRouter newRouter(WxCpService wxCpService) {
        WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 自定义菜单事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.MenuButtonType.CLICK)
            .handler(this.menuHandler).end();

        // 点击菜单链接事件（这里使用了一个空的处理器，可以根据自己需要进行扩展）
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.MenuButtonType.VIEW)
            .handler(this.nullHandler).end();

        // 关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.SUBSCRIBE)
            .handler(this.subscribeHandler).end();

        // 取消关注事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.UNSUBSCRIBE)
            .handler(this.unsubscribeHandler).end();

        // 上报地理位置事件
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.LOCATION)
            .handler(this.locationHandler).end();

        // 接收地理位置消息
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION).handler(this.locationHandler).end();

        // 扫码事件（这里使用了一个空的处理器，可以根据自己需要进行扩展）
        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.SCAN)
            .handler(this.nullHandler).end();

        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxCpConsts.EventType.CHANGE_CONTACT)
            .handler(new ContactChangeHandler()).end();

        newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxCpConsts.EventType.ENTER_AGENT)
            .handler(new EnterAgentHandler()).end();

        // 默认
        newRouter.rule().async(false).handler(this.msgHandler).end();

        return newRouter;
    }

}
