package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.user.UserQueryRequest;
import com.lyx.lopicture.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-01-13 17:04:57
* @Entity com.lyx.lopicture.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    Page<User> selectPage(Page<User> page, @Param("query") UserQueryRequest userQueryRequest);

}




