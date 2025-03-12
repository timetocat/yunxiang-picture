package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 * @description 针对表【post(帖子)】的数据库操作Mapper
 * @createDate 2025-02-18 16:04:53
 * @Entity generator.domain.Post
 */
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 分页查询帖子列表
     *
     * @param page             分页对象
     * @param postQueryRequest
     * @return
     */
    Page<Post> selectPage(Page<Post> page, @Param("query") PostQueryRequest postQueryRequest);

    /**
     * 更新帖子点赞数
     *
     * @param id     主键id
     * @param addNum 增加的点赞数
     * @return
     */
    int updateThumbNum(@Param("id") Long id, @Param("addNum") Integer addNum);

    /**
     * 更新帖子收藏数
     *
     * @param id
     * @param addNum
     * @return
     */
    int updateFavourNum(@Param("id") Long id, @Param("addNum") Integer addNum);

}




