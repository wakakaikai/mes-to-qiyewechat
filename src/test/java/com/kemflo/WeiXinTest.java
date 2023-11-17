package com.kemflo;

import com.kemflo.service.WeiXinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest

@Slf4j
public class WeiXinTest {
    @Resource
    private WeiXinService weiXinService;

    @Test
    public void simpleEmailTest() {
        String[] toArr = new String[]{"YueJianLi"};
        weiXinService.sendSimpleText(toArr, "你好啊,岳泽霖");
        log.info(">>>企业微信发送消息");
    }

    @Test
    public void velocityTest() {
        String[] toArr = new String[]{"YueJianLi"};
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("line",System.lineSeparator());
        dataMap.put("title","你叫什么名字");
        dataMap.put("content","我叫岳泽霖,是一个快乐的程序员");
        weiXinService.sendVelocityText("你好啊",toArr, dataMap,"interface_tenwhy.vm");
        log.info(">>>企业微信发送消息成功");
    }
}