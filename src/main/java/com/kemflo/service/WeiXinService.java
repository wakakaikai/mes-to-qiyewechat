package com.kemflo.service;

import java.util.Map;
/**
 * 邮件发送的接口信息
 *
 * @Author Kevin
 * @Date 2023/11/16 16:15
 */
public interface WeiXinService {
    /**
     * 发送普通的文本消息
     *  @param toArr   发送人， 之间用 ,号分隔
     * @param content 发送的内容, 普通文本内容
     */
    String sendSimpleText(String[] toArr, String content);

    /**
     * 发送邮件 velocity 模板邮件
     * @param title   主题
     * @param toArr                发送人
     * @param dataMap              发送模板邮件填充数据
     * @param templateName 模板名称
     */
    String sendVelocityText(String title, String[] toArr, Map<String, Object> dataMap, String templateName);
}
