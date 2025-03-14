package com.lyx.lopicture;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.lyx.lopicture.mapper")
@EnableMethodCache(basePackages = "com.lyx.lopicture")
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class LoPictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoPictureApplication.class, args);
    }

}
