package com.lyx.lopicture.service;

import com.lyx.lopicture.model.dto.postthumb.PostThumbAddRequest;
import com.lyx.lopicture.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.model.entity.User;

/**
 * @author Administrator
 * @description 针对表【post_thumb(帖子点赞)】的数据库操作Service
 * @createDate 2025-02-18 16:04:53
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 帖子 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param loginUser
     * @return
     */
    Integer doPostThumb(PostThumbAddRequest postThumbAddRequest, User loginUser);

    /**
     * 帖子点赞 / 取消点赞（内部服务）
     *
     * @param userId 点赞人id
     * @param postId 帖子id
     * @return
     */
    Integer doPostThumbInner(long userId, long postId);
}
