package com.kemflo.service;

public interface NoticeService {
    void sendTextMsg(String content);

    void sendMarkDownTextMsg(String content);

    void sendImageMsg(String content);

    void sendImageAndTxtMsg(String content);
}
