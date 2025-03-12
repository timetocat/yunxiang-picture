package com.lyx.lopicture.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.entity.Post;
import com.lyx.lopicture.model.entity.PostFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【post_favour(帖子收藏)】的数据库操作Mapper
* @createDate 2025-02-18 16:04:53
* @Entity generator.domain.PostFavour
*/
public interface PostFavourMapper extends BaseMapper<PostFavour> {

    /**
     * 分页查询收藏帖子列表
     *
     * @param page             分页对象
     * @param postQueryRequest
     * @param favourUser_id    被收藏的用户id
     * @return
     */
    Page<Post> listFavourPostByPage(Page<Post> page, @Param("query") PostQueryRequest postQueryRequest,
                                    @Param("favourUser_id") Long favourUser_id);

}




