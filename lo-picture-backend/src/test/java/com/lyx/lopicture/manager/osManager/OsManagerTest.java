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
}