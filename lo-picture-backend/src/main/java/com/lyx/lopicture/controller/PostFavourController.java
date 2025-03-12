package com.lyx.lopicture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.dto.post.PostQueryRequest;
import com.lyx.lopicture.model.dto.postfavour.PostFavourAddRequest;
import com.lyx.lopicture.model.dto.postfavour.PostFavourQueryRequest;
import com.lyx.lopicture.model.entity.Post;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.PostVO;
import com.lyx.lopicture.service.PostFavourService;
import com.lyx.lopicture.service.PostService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * @author lyx
 */
@RestController
@RequestMapping("/post_favour")
@RequiredArgsConstructor
public class PostFavourController {

    private final PostFavourService postFavourService;

    private final PostService postService;

    private final UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doPostFavour(@RequestBody PostFavourAddRequest postFavourAddRequest,
                                              HttpServletRequest request) {
        ThrowUtils.throwIf(postFavourAddRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(postFavourService.doPostFavour(postFavourAddRequest, userService.getLoginUser(request)));
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<PostVO>> listMyFavourPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                             HttpServletRequest request) {
        ThrowUtils.throwIf(postQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        Page<Post> postPage = postFavourService.listFavourPostByPage(postQueryRequest, loginUser.getId());
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<PostVO>> listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
                                                           HttpServletRequest request) {
        ThrowUtils.throwIf(postFavourQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(
                postFavourQueryRequest.getPostQueryRequest(), userId);
        return ResultUtils.success(postService.getPostVOPage(postPage, request));
    }
}
