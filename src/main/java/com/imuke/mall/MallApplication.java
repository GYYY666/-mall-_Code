package com.imuke.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.imuke.mall.dao")
public class MallApplication {

    public static void main(String[] args) {

         SpringApplication.run(MallApplication.class, args);

    }

}
