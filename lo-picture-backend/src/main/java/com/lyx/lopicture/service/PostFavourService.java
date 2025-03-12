package com.lyx.lopicture.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.dto.postfavour.PostFavourAddRequest;
import com.lyx.lopicture.model.entity.Post;
import com.lyx.lopicture.model.entity.PostFavour;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.model.entity.User;

/**
 * @author Administrator
 * @description 针对表【post_favour(帖子收藏)】的数据库操作Service
 * @createDate 2025-02-18 16:04:53
 */
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 帖子 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param loginUser
     * @return
     */
    Integer doPostFavour(PostFavourAddRequest postFavourAddRequest, User loginUser);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    Integer doPostFavourInner(Long userId, Long postId);

    /**
     * 分页获取用户收藏的帖子
     *
     * @param postQueryRequest
     * @param favourUserId
     * @return
     */
    Page<Post> listFavourPostByPage(PostQueryRequest postQueryRequest, Long favourUserId);
}
