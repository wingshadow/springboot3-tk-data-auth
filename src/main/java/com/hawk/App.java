package com.hawk;

import cn.hutool.extra.spring.EnableSpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Hello world!
 */
@SpringBootApplication(scanBasePackages = { "com.hawk" })
@MapperScan({"com.hawk.**.mapper"})
@EnableSpringUtil
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
