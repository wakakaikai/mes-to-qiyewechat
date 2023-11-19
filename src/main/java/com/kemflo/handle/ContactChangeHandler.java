package com.kemflo.handle;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.kemflo.builder.TextBuilder;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;

/**
 * 通讯录变更事件处理器.
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
@Slf4j
public class ContactChangeHandler extends AbstractHandler {
    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxMessage, Map<String, Object> context, WxCpService cpService,
        WxSessionManager sessionManager) {
        String content = "收到通讯录变更事件，内容：" + JSON.toJSONString(wxMessage);
        log.info(content);
        return new TextBuilder().build(content, wxMessage, cpService);
    }
}
