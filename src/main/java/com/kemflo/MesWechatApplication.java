package com.kemflo;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@ForestScan(basePackages = "com.kemflo.remote")
@SpringBootApplication
public class MesWechatApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesWechatApplication.class, args);
    }

}
