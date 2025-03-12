package com.lyx.lopicture.controller;

import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.dto.postthumb.PostThumbAddRequest;
import com.lyx.lopicture.service.PostThumbService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/post_thumb")
@RequiredArgsConstructor
public class PostThumbController {

    private final PostThumbService postThumbService;

    private final UserService userService;

    /**
     * 帖子 点赞 / 取消点赞
     *
     * @param postThumbAddRequest 帖子点赞请求
     * @param request             http请求
     * @return 帖子点赞状态码
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
                                         HttpServletRequest request) {
        ThrowUtils.throwIf(postThumbAddRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(postThumbService.doPostThumb(postThumbAddRequest,
                userService.getLoginUser(request)));
    }

}
