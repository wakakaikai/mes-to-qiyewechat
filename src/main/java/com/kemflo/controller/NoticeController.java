package com.kemflo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dtflys.forest.annotation.NotNull;
import com.kemflo.service.NoticeService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/sendMsg")
    public String sendMsg(@NotNull @RequestParam("testType") String testType) {
        switch (testType) {
            case "1":
                noticeService.sendTextMsg("文本消息测试");
                break;
            case "2":
                noticeService.sendMarkDownTextMsg("markdown消息测试");
                break;
            case "3":
                noticeService.sendImageMsg("图片消息测试");
                break;
            case "4":
                noticeService.sendImageAndTxtMsg("图文消息测试");
                break;
            default:
                log.error("不处理任何信息");
        }
        return "success";
    }
}
