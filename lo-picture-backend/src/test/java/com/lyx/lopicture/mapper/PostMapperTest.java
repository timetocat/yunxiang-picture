package com.lyx.lopicture.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostMapperTest {

    @Resource
    private PostMapper postMapper;

    @Test
    void updateThumbNumInteger() {
        int i = postMapper.updateThumbNum(1L, -1);
        assertTrue(i > 0);
    }
}