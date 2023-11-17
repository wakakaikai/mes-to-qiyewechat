package com.kemflo.model;

import lombok.Data;

/**
 * @Description 微信通用接口凭证, 获取token信息
 * @Author Kevin
 * @Date 2023/11/16 16:15
 **/
@Data
public class AccessToken {
    /**
     * 获取到的凭证
     */
    private String token;
    /**
     * 凭证有效时间，单位：秒
     */
    private int expiresIn;
}
