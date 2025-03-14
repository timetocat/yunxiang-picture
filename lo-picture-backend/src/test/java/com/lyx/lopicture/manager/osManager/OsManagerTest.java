package com.lyx.lopicture.manager.osManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class OsManagerTest {

    @Resource
    private OsManager osManager;


    @Test
    void getPictureUrl() {
        String pictureUrl = osManager.getPictureUrl("testss/640.png");
        Assertions.assertNotNull(pictureUrl);
        System.out.println(pictureUrl);
    }

    @Test
    void deleteObject() {
        // String url = "http://192.168.1.103:9000/lopic/public/1/2025-03-14_OSQWrH8LsToVydcy.webp";
        String url = /*cosClientConfig.getHost() + */ "/public/1/2025-03-14_7Yd9ZTBHZhlqWLKd.jpg";
        osManager.deleteObject(url);
    }
}