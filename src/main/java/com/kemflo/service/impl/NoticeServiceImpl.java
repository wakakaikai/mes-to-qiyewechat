package com.kemflo.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

import com.kemflo.config.WxCpProperties;
import me.chanjar.weixin.common.util.fs.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kemflo.remote.WechatNoticeClient;
import com.kemflo.service.NoticeService;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import sun.misc.BASE64Encoder;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private WechatNoticeClient wechatNoticeClient;
    @Autowired
    private WxCpProperties wxCpProperties;


    /**
     * 发送文本消息
     */
    @Override
    public void sendTextMsg(String content) {
        Map<String, Object> sendMap = new HashMap<>();
        // 设置消息类型 txt文本
        sendMap.put("msgtype", WxConsts.MassMsgType.TEXT);
        // 消息内容
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", content);
        sendMap.put("text", contentMap);
        // 表示是否开启重复消息检查，0表示否，1表示是，默认0
        sendMap.put("enable_duplicate_check", 1);
        // 表示是否重复消息检查的时间间隔，默认1800s，最大不超过4小时
        sendMap.put("duplicate_check_interval", 1800);
        wechatNoticeClient.sendRobotMsg(wxCpProperties.getRobotUrl(), sendMap);
    }

    /**
     * 发送markdown文本消息
     */
    @Override
    public void sendMarkDownTextMsg(String content) {
        Map<String, Object> sendMap = new HashMap<>();
        // 设置消息类型 markdown文本
        sendMap.put("msgtype", "markdown");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content",
            "您的会议室已经预定，稍后会同步到`邮箱`  \n>**事项详情**  \n>事　项：<font color=\"info\">开会</font>  \n>组织者：@miglioguan  \n>参与者：@miglioguan、@kunliu、@jamdeezhou、@kanexiong、@kisonwang  \n>  \n>会议室：<font color=\"info\">广州TIT 1楼 301</font>  \n>日　期：<font color=\"warning\">2018年5月18日</font>  \n>时　间：<font color=\"comment\">上午9:00-11:00</font>  \n>  \n>请准时参加会议。  \n>  \n>如需修改会议信息，请点击：[修改会议信息](https://work.weixin.qq.com)");
        sendMap.put("markdown", contentMap);
        wechatNoticeClient.sendRobotMsg(wxCpProperties.getRobotUrl(), sendMap);
    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImageMsg(String content) {
        String url = "https://wework.qpic.cn/wwpic/556466_GbaFIr-4SLqP4uH_1700310830/0";
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("msgtype", "image");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("md5", getMd5(url));
        contentMap.put("base64", getBase64(url).replaceAll("\r|\n", ""));
        sendMap.put("image", contentMap);
        wechatNoticeClient.sendRobotMsg(wxCpProperties.getRobotUrl(), sendMap);
    }

    /**
     * 发送图文消息
     */
    @Override
    public void sendImageAndTxtMsg(String content) {
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("msgtype", "news");
        Map<String, Object> contentMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> obj = new HashMap<>();
        obj.put("title", "yst-mes");
        obj.put("description", content);
        obj.put("url", "https://mes.yakimagroup.com:8999/");
        obj.put("picurl", "https://wework.qpic.cn/wwpic/556466_GbaFIr-4SLqP4uH_1700310830/0");
        list.add(obj);
        contentMap.put("articles", list);
        contentMap.put("mentioned_list", Arrays.asList("@all"));
        sendMap.put("news", contentMap);
        wechatNoticeClient.sendRobotMsg(wxCpProperties.getRobotUrl(), sendMap);
    }

    /**
     * 图片转为base64编码
     */
    public String getBase64(String imgFile) {
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(data);
    }

    /**
     * 获取文件的MD5值
     *
     * @param path
     * @return
     */
    public String getMd5(String path) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(path);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
            fis.close();
            byte[] byteArray = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : byteArray) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
