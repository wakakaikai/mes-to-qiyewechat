package com.kemflo.handle;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;

@Component
@Slf4j
public class MenuHandler extends AbstractHandler {
    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context, WxCpService cpService,
        WxSessionManager sessionManager) {

        String msg = String.format("type:%s, event:%s, key:%s", wxMessage.getMsgType(), wxMessage.getEvent(),
            wxMessage.getEventKey());
        if (WxConsts.MenuButtonType.VIEW.equals(wxMessage.getEvent())) {
            return null;
        }

        return WxCpXmlOutMessage.TEXT().content(msg).fromUser(wxMessage.getToUserName())
            .toUser(wxMessage.getFromUserName()).build();
    }

}