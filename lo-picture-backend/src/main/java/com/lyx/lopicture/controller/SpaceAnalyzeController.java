package com.lyx.lopicture.controller;

import com.lyx.lopicture.annotation.AuthCheck;
import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.dto.space.analyze.*;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.vo.space.analyze.*;
import com.lyx.lopicture.service.SpaceAnalyzeService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/space/analyze")
@RequiredArgsConstructor
public class SpaceAnalyzeController {

    private final UserService userService;

    private final SpaceAnalyzeService spaceAnalyzeService;

    /**
     * 获取空间的使用状态
     *
     * @param spaceUsageAnalyzeRequest 空间资源使用分析请求
     * @param request
     * @return
     */
    @PostMapping("/usage")
    public BaseResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(
            @RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取空间图片分类分析
     *
     * @param spaceCategoryAnalyzeRequest 获取空间图片分类分析请求
     * @param request
     * @return
     */
    @PostMapping("/category")
    public BaseResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(
            @RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取空间图片标签分析
     *
     * @param spaceTagAnalyzeRequest 获取空间图片标签分析请求
     * @param request
     * @return
     */
    @PostMapping("/tag")
    public BaseResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(
            @RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceTagAnalyze(spaceTagAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取空间图片大小分析
     *
     * @param spaceSizeAnalyzeRequest 获取空间图片大小分析请求
     * @param request
     * @return
     */
    @PostMapping("/size")
    public BaseResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze
    (@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取空间用户行为分析
     *
     * @param spaceUserAnalyzeRequest 获取空间用户行为分析请求
     * @param request
     * @return
     */
    @PostMapping("/user")
    public BaseResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze
    (@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取空间使用排行分析
     *
     * @param spaceRankAnalyzeRequest 获取空间使用排行分析请求
     * @param request
     * @return
     */
    @PostMapping("/rank")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<Space>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest,
                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取图片空间等级分析
     *
     * @param spaceLevelAnalyzeRequest
     * @param request
     * @return
     */
    @PostMapping("/level")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<SpaceLevelAnalyzeResponse>> getSpaceLevelAnalyze(@RequestBody SpaceLevelAnalyzeRequest spaceLevelAnalyzeRequest,
                                                                              HttpServletRequest request) {
        ThrowUtils.throwIf(spaceLevelAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceLevelAnalyze(spaceLevelAnalyzeRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取图片空间审核分析
     *
     * @param spaceReviewAnalyzeRequest
     * @param request
     * @return
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<SpaceReviewAnalyzeResponse>> reviewSpace(@RequestBody SpaceReviewAnalyzeRequest spaceReviewAnalyzeRequest,
                                                                      HttpServletRequest request) {
        ThrowUtils.throwIf(spaceReviewAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceAnalyzeService.getSpaceReviewAnalyze(spaceReviewAnalyzeRequest,
                userService.getLoginUser(request)));
    }
}

