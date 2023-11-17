package com.kemflo.common;

import org.springframework.context.annotation.Configuration;

/**
 * @author Kevin
 * @Date: 2022/8/23 18:46
 * @Description: 常量类
 */
@Configuration
public class WxConstants {
    public WxConstants() {
    }

    /**
     * 请求api需要的token，开启定时任务，每2个小时获取一次
     */
    public static String accessToken = "";
}
