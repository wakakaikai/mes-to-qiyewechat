package com.kemflo.model;

import lombok.Data;

/**
 * @Description 文本是否加密的信息
 * @Author Kevin
 * @Date 2023/11/16 16:15
 **/
@Data
public class TextMessage extends BaseMessage{
    /**
     * 文本
     */
    private WxText text;
    /**
     * 否 表示是否是保密消息，0表示否，1表示是，默认0
     */
    private int safe;
}
