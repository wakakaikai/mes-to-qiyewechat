package com.kemflo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 该类是在程序启动时，将企业微信服务商的配置信息映射到该属性类中。
 * 
 * @author Kevin
 * @create 2023-11-05 16:14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@ConfigurationProperties(prefix = "wechat")
public class WxCpProperties {
    /**
     * 设置微信企业号的corpId
     */
    private String corpId;

    private List<AppConfig> appConfigs;

    /**
     * 服务商的应用Id
     */
    private Integer agentId;
    /**
     * 服务商的secret
     */
    private String corpSecret;
    /**
     * 服务商的token
     */
    private String token;
    /**
     * 服务商的EncodingAESKey
     */
    private String aesKey;

    /**
     * 群机器人发送消息
     */
    private String robotUrl;

    /**
     * 获取access_token
     */
    private String tokenUrl;

    /**
     * 发送应用消息
     */
    private String sendMsgUrl;
}
