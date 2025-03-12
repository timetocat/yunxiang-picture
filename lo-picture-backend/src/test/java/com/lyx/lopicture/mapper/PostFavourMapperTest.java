package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostFavourMapperTest {

    @Resource
    private PostFavourMapper postFavourMapper;

    @Test
    void listFavourPostByPage() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setTags(List.of("java", "go"));
        Page<Post> postPage = postFavourMapper.listFavourPostByPage(new Page<>(0, 10),
                postQueryRequest, 2L);
        postQueryRequest.setTags(List.of("java", "python"));
        Page<Post> postPage2 = postFavourMapper.listFavourPostByPage(new Page<>(0, 10),
                postQueryRequest, 2L);
        assertTrue(postPage.getTotal() > 0);
        assertTrue(postPage2.getTotal() > 0);
    }
}