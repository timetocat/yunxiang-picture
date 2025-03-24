package com.lyx.lopicture.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyx.lopicture.annotation.AuthCheck;
import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.dto.user.*;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.LoginUserVO;
import com.lyx.lopicture.model.vo.UserVO;
import com.lyx.lopicture.service.UserService;
import com.lyx.lopicture.utils.CaptchaUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userRegister(userRegisterRequest));
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                               HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogin(userLoginRequest, request));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request));
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.addUser(userAddRequest));
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteRequest userDeleteRequest) {
        ThrowUtils.throwIf(userDeleteRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.deleteUser(userDeleteRequest));
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.updateUser(userUpdateRequest));
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> editUser(@RequestBody UserEditRequest userEditRequest,
                                          HttpServletRequest request) {
        ThrowUtils.throwIf(userEditRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.editUser(userEditRequest, userService.getLoginUser(request)));
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.getUserVO(user));
    }

    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.getUserPage(userQueryRequest);
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        userVOPage.setRecords(userService.getUserVOList(userPage.getRecords()));
        return ResultUtils.success(userVOPage);
    }

    @PostMapping("/update/password")
    public BaseResponse<Boolean> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest,
                                                HttpServletRequest request) {
        ThrowUtils.throwIf(passwordUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.updatePassword(passwordUpdateRequest, request));
    }

    /**
     * 兑换会员
     */
    @PostMapping("/exchange/vip")
    public BaseResponse<Boolean> exchangeVip(@RequestBody VipExchangeRequest vipExchangeRequest,
                                             HttpServletRequest request) {
        ThrowUtils.throwIf(vipExchangeRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userService.exchangeVip(vipExchangeRequest,
                userService.getLoginUser(request)));
    }

    @GetMapping("/getcode")
    public BaseResponse<Map<String, String>> getCode() {
        return ResultUtils.success(CaptchaUtils.getCaptcha());
    }

    /**
     * 获取邮箱验证码
     */
    @PostMapping("/get_emailcode")
    public BaseResponse<String> getEmailCode(@RequestBody EmailCodeRequest emailCodeRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(emailCodeRequest == null, ErrorCode.PARAMS_ERROR);
        userService.sendEmailCode(emailCodeRequest, request);
        return ResultUtils.success("验证码发送成功");
    }
}
