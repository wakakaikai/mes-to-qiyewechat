package com.kemflo.controller;

import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson2.JSON;
import com.kemflo.config.WxCpConfiguration;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;

/**
 * 企业微信认证接口
 *
 * @author Kevin
 */
@Slf4j
@RestController
@RequestMapping("/cp/portal/{agentId}")
public class WxCpPortalController {


    /**
     * 请求用于企业微信服务器验证 URL 的有效性。
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String auth(@PathVariable  String agentId, @RequestParam(name = "msg_signature") String signature,
        @RequestParam(name = "timestamp") String timestamp, @RequestParam(name = "nonce") String nonce,
        @RequestParam(name = "echostr") String echostr) {
        try {
            log.info(
                "接收到来自微信服务器的认证消息：\nagentId = [{}],\nsignature = [{}], \ntimestamp = [{}], \nnonce = [{}], \nechostr = [{}]",
                    agentId, signature, timestamp, nonce, echostr);
            if (StringUtils.isBlank(agentId)) {
                throw new IllegalArgumentException(String.format("未找到对应agentId=[%s]的配置，请核实！", agentId));
            }
            if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
                log.error("请求参数非法，请核实!");
                throw new IllegalArgumentException("请求参数非法，请核实!");
            }
            WxCpService wxCpService = WxCpConfiguration.getCpService(Integer.valueOf(agentId));
            if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
                return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
            }
        } catch (Exception e) {
            log.error("URL验证处理异常：", e);
        }
        return "非法请求";
    }

    /**
     * 请求用于处理企业微信推送过来的消息
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String agentId, @RequestBody String requestBody,
        @RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
        @RequestParam("nonce") String nonce) {
        try {
            log.info("接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[{}] ", signature, timestamp,
                nonce, requestBody);
            if (StringUtils.isBlank(agentId)) {
                throw new IllegalArgumentException(String.format("未找到对应agentId=[%s]的配置，请核实！", agentId));
            }
            WxCpConfigStorage wxCpConfigStorage = WxCpConfiguration.getCpService(Integer.valueOf(agentId)).getWxCpConfigStorage();
            WxCpXmlMessage inMessage =
                WxCpXmlMessage.fromEncryptedXml(requestBody, wxCpConfigStorage, timestamp, nonce, signature);
            log.info("消息解密后内容为：{} ", JSON.toJSONString(inMessage));
            WxCpXmlOutMessage outMessage = WxCpConfiguration.getRouter(Integer.valueOf(agentId)).route(inMessage);
            if (outMessage == null) {
                return "";
            }
            return outMessage.toEncryptedXml(wxCpConfigStorage);
        } catch (Exception e) {
            log.error("消息处理异常：", e);
        }
        return "";
    }
}
