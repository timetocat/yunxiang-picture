package com.lyx.lopicture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.dto.postfavour.PostFavourAddRequest;
import com.lyx.lopicture.model.entity.Post;
import com.lyx.lopicture.model.entity.PostFavour;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.PostFavourService;
import com.lyx.lopicture.mapper.PostFavourMapper;
import com.lyx.lopicture.service.PostService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @description 针对表【post_favour(帖子收藏)】的数据库操作Service实现
 * @createDate 2025-02-18 16:04:53
 */
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
        implements PostFavourService {

    @Resource
    private PostService postService;

    /**
     * 帖子 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Integer doPostFavour(PostFavourAddRequest postFavourAddRequest, User loginUser) {
        Long postId = postFavourAddRequest.postId();
        // 检测帖子是否存在
        postService.checkPostExist(postId);
        Long userId = loginUser.getId();
        PostFavourService postFavourService = (PostFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postFavourService.doPostFavourInner(userId, postId);
        }
    }

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    @Override
    public Integer doPostFavourInner(Long userId, Long postId) {
        PostFavour postFavour = new PostFavour();
        postFavour.setUserId(userId);
        postFavour.setPostId(postId);
        QueryWrapper<PostFavour> postFavourQueryWrapper = new QueryWrapper<>(postFavour);
        PostFavour oldPostFavour = this.getOne(postFavourQueryWrapper);
        boolean result = oldPostFavour != null ? this.remove(postFavourQueryWrapper) : // 已收藏，取消收藏
                this.save(postFavour); // 未收藏，收藏
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        int addNum = oldPostFavour == null ? 1 : -1;
        Integer res = postService.updatePostFavour(postId, addNum);
        return res > 0 ? addNum : 0;
    }

    /**
     * 分页获取用户收藏的帖子
     *
     * @param postQueryRequest
     * @param favourUserId
     * @return
     */
    @Override
    public Page<Post> listFavourPostByPage(PostQueryRequest postQueryRequest, Long favourUserId) {
        Page<Post> page = new Page<>(postQueryRequest.getCurrent(), postQueryRequest.getPageSize());
        return this.baseMapper.listFavourPostByPage(page, postQueryRequest, favourUserId);
    }

}




