package com.kemflo.model;

import lombok.Data;

/**
 * @Description TextCard 类型，支持 html 标签
 * @Author Kevin
 * @Date 2023/11/16 16:15
 **/
@Data
public class TextCardMessage extends BaseMessage{
    /**
     * 放置内容
     */
    private WxTextCard textcard;
}
