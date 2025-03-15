package com.lyx.lopicture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.annotation.AuthCheck;
import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.DeleteRequest;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.dto.space.SpaceAddRequest;
import com.lyx.lopicture.model.dto.space.SpaceEditRequest;
import com.lyx.lopicture.model.dto.space.SpaceQueryRequest;
import com.lyx.lopicture.model.dto.space.SpaceUpdateRequest;
import com.lyx.lopicture.model.entity.Space;
import com.lyx.lopicture.model.enums.SpaceLevelEnum;
import com.lyx.lopicture.model.enums.spacelevel.SpaceLevel;
import com.lyx.lopicture.model.vo.SpaceVO;
import com.lyx.lopicture.service.SpaceService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class SpaceController {

    private final UserService userService;

    private final SpaceService spaceService;

    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceService.addSpace(spaceAddRequest,
                userService.getLoginUser(request)));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest
            , HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceService.deleteSpace(deleteRequest.getId(),
                userService.getLoginUser(request)));
    }

    /**
     * 更新空间（仅管理员可用）
     *
     * @param spaceUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest,
                                             HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceService.updateSpace(spaceUpdateRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(space);
    }

    /**
     * 根据 id 获取空间（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(spaceService.getSpaceVO(space, request));
    }

    /**
     * 分页获取空间列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceService.getSpacePage(spaceQueryRequest));
    }

    /**
     * 分页获取空间列表（封装类）
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                         HttpServletRequest request) {
        long size = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Space> picturePage = spaceService.getSpacePage(spaceQueryRequest);
        // 获取封装类
        return ResultUtils.success(spaceService.getSpaceVOPage(picturePage, request));
    }

    /**
     * 编辑空间（给用户使用）
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceEditRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceService.editSpace(spaceEditRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 获取空间级别列表，便于前端展示
     *
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        return ResultUtils.success(BaseValueEnum.getValues(SpaceLevelEnum.class, SpaceLevelEnum.RETURN_TYPE));
    }
}
