package com.lyx.lopicture.controller;

import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.annotation.AuthCheck;
import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.DeleteRequest;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.manager.auth.annotation.SaSpaceCheckPermission;
import com.lyx.lopicture.manager.auth.model.SpaceUserPermissionConstant;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserAddRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserEditRequest;
import com.lyx.lopicture.model.dto.spaceuser.SpaceUserQueryRequest;
import com.lyx.lopicture.model.entity.SpaceUser;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.SpaceUserVO;
import com.lyx.lopicture.service.SpaceUserService;
import com.lyx.lopicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/spaceUser")
@RequiredArgsConstructor
@Slf4j
public class SpaceUserController {

    private final UserService userService;

    private final SpaceUserService spaceUserService;

    /**
     * 添加成员到空间
     */
    @PostMapping("/add")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceUserService.addSpaceUser(spaceUserAddRequest));
    }

    /**
     * 从空间移除成员
     */
    @PostMapping("/delete")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest
            , HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceUserService.deleteSpaceUser(deleteRequest.getId(),
                userService.getLoginUser(request)));
    }

    /**
     * 查询每个成员在某个空间的信息
     */
    @PostMapping("/get")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<SpaceUser> getSpaceUserById(@RequestBody SpaceUserQueryRequest spaceUserQueryRequest,
                                                    HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceUserQueryRequest.spaceId(),
                spaceUserQueryRequest.userId()), ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceUserService.getSpaceUserInfo(spaceUserQueryRequest,
                userService.getLoginUser(request)));
    }


    /**
     * 编辑成员信息（设置权限）
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceUserEditRequest spaceUserEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserEditRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(spaceUserService.editSpaceUser(spaceUserEditRequest,
                userService.getLoginUser(request)));
    }

    /**
     * 查询成员信息列表
     */
    @PostMapping("/list")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<SpaceUserVO>> listSpaceUserByPage(@RequestBody SpaceUserQueryRequest spaceQueryRequest,
                                                               HttpServletRequest request) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService
                .list(spaceUserService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList,
                userService.getLoginUser(request)));
    }


    /**
     * 查询我加入的团队空间列表
     */
    @PostMapping("/list/my")
    public BaseResponse<List<SpaceUserVO>> listMyTeamSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(SpaceUserQueryRequest.builder()
                        .userId(loginUser.getId())
                        .build())
        );
        return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList, loginUser));
    }
}
