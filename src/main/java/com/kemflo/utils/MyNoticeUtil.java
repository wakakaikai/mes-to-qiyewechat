package com.kemflo.utils;
import cn.hutool.core.io.FileUtil;
import com.kemflo.remote.WechatNoticeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

@Component
public class MyNoticeUtil {
    @Autowired
    private WechatNoticeClient wechatNoticeClient;

    @Value("${wechat.notice.key}")
    private String NOTICE_KEY;

    /**
     * 发送文本消息
     */
    public void sendTextMsg() {
        Map<String, Object> sendMap = new HashMap<>();
        //设置消息类型 txt文本
        sendMap.put("msgtype", "text");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", "你好，我是资讯测试机器人");
        sendMap.put("text", contentMap);
        wechatNoticeClient.sendWechatMsg(NOTICE_KEY, sendMap);
    }

    /**
     * 发送markdown文本消息
     */
    public void sendMarkDownTextMsg() {
        Map<String, Object> sendMap = new HashMap<>();
        //设置消息类型 markdown文本
        sendMap.put("msgtype", "markdown");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", "JCccc,您的账户余额已到账<font color=\\\"warning\\\">15000元</font>，开心起来吧。\\\n" +
                "         >付款方:<font color=\\\"comment\\\">白日做梦</font>");
        sendMap.put("markdown", contentMap);
        wechatNoticeClient.sendWechatMsg(NOTICE_KEY, sendMap);
    }

    /**
     * 发送图片消息
     */
    public void sendImageMsg() {
        String url = "D:\\projects\\mes-to-qiyewechat\\src\\main\\resources\\static\\alarm.png";
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("msgtype", "image");
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("md5", getMd5(url));
        contentMap.put("base64", getBase64(url).replaceAll("\r|\n", ""));
        sendMap.put("image", contentMap);
        wechatNoticeClient.sendWechatMsg(NOTICE_KEY, sendMap);
    }

    /**
     * 发送图文消息
     */
    public void sendImageAndTxtMsg(String content) {
        Map<String, Object> sendMap = new HashMap<>();
        sendMap.put("msgtype", "news");
        Map<String, Object> contentMap = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> obj = new HashMap<>();
        obj.put("title", "yst-mes");
        obj.put("description", content);
        obj.put("url","https://mes.yakimagroup.com:8999/");
        obj.put("picurl", "http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png");
        list.add(obj);
        contentMap.put("articles", list);
        contentMap.put("mentioned_list", Arrays.asList("@all"));
        sendMap.put("news", contentMap);
        wechatNoticeClient.sendWechatMsg(NOTICE_KEY, sendMap);
    }


    /**
     * 图片转为base64编码
     */
    public String getBase64(String imgFile) {
        InputStream in = null;
        byte[] data = null;
        //  读取图片字节数组
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
