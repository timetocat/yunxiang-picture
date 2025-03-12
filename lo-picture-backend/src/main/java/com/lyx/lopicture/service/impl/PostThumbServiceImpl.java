package com.lyx.lopicture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.mapper.PostThumbMapper;
import com.lyx.lopicture.model.dto.postthumb.PostThumbAddRequest;
import com.lyx.lopicture.model.entity.PostThumb;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.PostService;
import com.lyx.lopicture.service.PostThumbService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @description 针对表【post_thumb(帖子点赞)】的数据库操作Service实现
 * @createDate 2025-02-18 16:04:53
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    /**
     * 帖子 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Integer doPostThumb(PostThumbAddRequest postThumbAddRequest, User loginUser) {
        Long postId = postThumbAddRequest.postId();
        // 检测帖子是否存在
        postService.checkPostExist(postId);
        Long userId = loginUser.getId();
        PostThumbService postThumbService = (PostThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postThumbService.doPostThumbInner(userId, postId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer doPostThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> thumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(thumbQueryWrapper);
        boolean result = oldPostThumb != null ? this.remove(thumbQueryWrapper) : // 已点赞，取消点赞
                this.save(postThumb); // 未点赞，点赞
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        int addNum = oldPostThumb == null ? 1 : -1;
        Integer res = postService.updatePostThumb(postId, addNum);
        return res > 0 ? addNum : 0;
    }
}




