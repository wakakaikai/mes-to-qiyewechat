package com.kemflo.controller;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Kevin
 * @Date: 2022/8/27 13:39
 * @Description: 定时任务
 */
@RestController
@EnableScheduling
@Slf4j
public class TimedTaskController {

    /**
     * 给特殊的人发早安（SPECIAL_MORNING模板）
     */
    @PostMapping("/executeSpecialMorningTask")
    @Scheduled(cron = "0 0 7 * * ?")
    private void executeSpecialMorningTask() {

    }

    /**
     * 定时任务调用
     */

    @Scheduled(fixedRate = 1 * 3600 * 1000, initialDelay = 10)
    @PostMapping("/acquireAccessToken")
    public void acquireAccessToken() {

    }
}
