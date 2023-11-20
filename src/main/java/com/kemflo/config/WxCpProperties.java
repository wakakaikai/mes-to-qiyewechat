package com.kemflo.config;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 该类是在程序启动时，将企业微信服务商的配置信息映射到该属性类中。
 * 
 * @author Kevin
 * @create 2023-11-05 16:14
 **/
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "wechat.cp")
public class WxCpProperties {
    /**
     * 设置企业微信的corpId
     */
    private String corpId;

    private List<AppConfig> appConfigs;

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

    @Getter
    @Setter
    public static class AppConfig {
        /**
         * 设置企业微信应用的AgentId
         */
        private Integer agentId;

        /**
         * 设置企业微信应用的Secret
         */
        private String secret;

        /**
         * 设置企业微信应用的token
         */
        private String token;

        /**
         * 设置企业微信应用的EncodingAESKey
         */
        private String aesKey;

    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
