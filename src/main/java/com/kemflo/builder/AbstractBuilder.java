package com.kemflo.builder;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;

public abstract class AbstractBuilder {
    /**
     * 创建WxCpXmlOutMessage信息
     * 
     * @param content
     * @param wxMessage
     * @param service
     * @return
     */
    public abstract WxCpXmlOutMessage build(String content, WxCpXmlMessage wxMessage, WxCpService service);
}
