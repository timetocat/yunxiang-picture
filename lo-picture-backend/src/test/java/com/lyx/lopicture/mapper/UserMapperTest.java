package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.common.SortFieldPair;
import com.lyx.lopicture.model.dto.user.UserQueryRequest;
import com.lyx.lopicture.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

@SpringBootTest
class UserMapperTest {

    @Resource
    private UserMapper userMapper;

    @Test
    void selectPage() {
        Page<User> page = new Page<>(0, 10);
        List<SortFieldPair> sortFieldPairs = List.of(
                new SortFieldPair("id", "ascend"),
                new SortFieldPair("user_account", "descend")
        );
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setSortFieldPairs(sortFieldPairs);
        Page<User> userPage = userMapper.selectPage(page, queryRequest);
        System.out.println(userPage);
    }
}