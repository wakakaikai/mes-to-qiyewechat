package com.kemflo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.dtflys.forest.springboot.annotation.ForestScan;

@ForestScan(basePackages = "com.kemflo.remote")
@SpringBootApplication
@EnableConfigurationProperties
public class WechatCpApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatCpApplication.class, args);
    }

}
