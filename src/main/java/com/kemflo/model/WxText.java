package com.kemflo.model;

import lombok.Data;

/**
 * @Description 普通微信文本信息
 * @Author Kevin
 * @Date 2023/11/16 16:15
 **/
@Data
public class WxText {
    /**
     * 是消息内容，最长不超过2048个字节
     */
    private String content;
}
