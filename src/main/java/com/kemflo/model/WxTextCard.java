package com.kemflo.model;

import lombok.Data;

/**
 * @Description markdown 文本信息
 * @Author Kevin
 * @Date 2023/11/16 16:15
 **/
@Data
public class WxTextCard {
    /**
     * 标题
     */
    private String title;
    /**
     * 描述信息
     */
    private String description;
    /**
     * url 信息
     */
    private String url;
    /**
     * 按钮展示文字
     */
    private String btntxt;
}
