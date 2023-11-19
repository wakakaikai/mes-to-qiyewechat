package com.kemflo.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson2.JSON;
import com.kemflo.config.WxCpConfiguration;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;

/**
 * 企业微信
 *
 * @author Kevin
 */
@Slf4j
@RestController
@RequestMapping("/cp/portal")
public class WxCpPortalController {

    private WxCpService wxCpService;

    /**
     * 用于企业微信服务器验证 URL 的有效性
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String auth(@RequestParam(name = "msg_signature") String signature,
        @RequestParam(name = "timestamp") String timestamp, @RequestParam(name = "nonce") String nonce,
        @RequestParam(name = "echostr") String echostr) {
        try {
            log.info("接收到来自微信服务器的认证消息：\nsignature = [{}], \ntimestamp = [{}], \nnonce = [{}], \nechostr = [{}]",
                signature, timestamp, nonce, echostr);
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                log.error("请求参数非法，请核实!");
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }
            if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
                return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
            }
        } catch (Exception e) {
            log.error("URL验证处理异常：{}", e.getMessage());
        }
        return "非法请求";
    }

    /**
     * 请求用于处理企业微信推送过来的消息
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody, @RequestParam("msg_signature") String signature,
        @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) {
        try {
            log.info("接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[{}] ", signature, timestamp,
                nonce, requestBody);
            WxCpXmlMessage inMessage = WxCpXmlMessage.fromEncryptedXml(requestBody, wxCpService.getWxCpConfigStorage(),
                timestamp, nonce, signature);
            log.info("消息解密后内容为：{} ", JSON.toJSONString(inMessage));
            WxCpXmlOutMessage outMessage = this.route(inMessage.getAgentId(), inMessage);
            if (outMessage == null) {
                return "";
            }
            String out = outMessage.toEncryptedXml(wxCpService.getWxCpConfigStorage());
            log.info("接收消息服务器处理异常：{}", out);
            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private WxCpXmlOutMessage route(String agentId, WxCpXmlMessage message) {
        try {
            return WxCpConfiguration.getRouters().get(agentId).route(message);
        } catch (Exception e) {
            log.error("转发处理路由异常{}", e);
        }
        return null;
    }
}
