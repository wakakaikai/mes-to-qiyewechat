package com.kemflo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MsgTypeEnum {
    MSG_TYPE_TEXT("text", "文本消息"), MSG_TYPE_IMAGE("image", "图文消息"), MSG_TYPE_VOICE("voice", "语音消息"),
    MSG_TYPE_FILE("file", "文件消息"), MSG_TYPE_TEXT_CARD("textcard", "文本卡片消息"), MSG_TYPE_news("news", "图文消息"),
    MSG_TYPE_MPNEWS("news", "图文消息"), MSG_TYPE_MARKDOWN("markdown", "图文消息，图文内容存储在企业微信"),
    MSG_TYPE_MINIPROGRAM_NOTICE("miniprogram_notice", "小程序通知消息"), MSG_TYPE_TEMPLATE_CARD("template_card", "模板卡片消息");

    private String value;

    private String desc;
}
