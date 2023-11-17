package com.kemflo.controller;


import com.kemflo.common.WxConstants;
import com.kemflo.service.InitService;
import com.kemflo.utils.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: Kevin
 * @Date: 2022/8/27 13:39
 * @Description: 定时任务
 */
@RestController
@EnableScheduling
@Slf4j
public class TimedTaskController {
    @Autowired
    private InitService initService;

    /**
     * 给特殊的人发早安（SPECIAL_MORNING模板）
     */
    @PostMapping("/executeSpecialMorningTask")
    @Scheduled(cron = "0 0 7 * * ?")
    private void executeSpecialMorningTask() {

    }

    /**
     * 一个小时获取一次accessToken
     */

    @Scheduled(fixedRate = 60 * 60 * 1000, initialDelay = 10)
    @PostMapping("/acquireAccessToken")
    public void  acquireAccessToken() {
        initService.init();
    }
}
