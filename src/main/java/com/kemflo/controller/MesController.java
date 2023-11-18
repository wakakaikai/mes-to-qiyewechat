package com.kemflo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dtflys.forest.annotation.NotNull;
import com.kemflo.service.MesService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MesController {
    @Autowired
    private MesService mesService;

    @GetMapping("/sendToMes")
    public String sendMsg(@NotNull @RequestParam("systemName") String systemName) {
        return mesService.sendMesSystem(systemName);
    }
}
