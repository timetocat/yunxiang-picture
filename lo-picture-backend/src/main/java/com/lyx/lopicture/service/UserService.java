package com.lyx.lopicture.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyx.lopicture.model.dto.user.*;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.vo.LoginUserVO;
import com.lyx.lopicture.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-01-13 17:04:57
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新用户id
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          http请求
     * @return 已登录用户视图
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request http请求
     * @return 未脱敏用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request http请求
     * @return 未脱敏用户信息
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 获取当前登录用户视图
     *
     * @param user 用户信息
     * @return 用户视图
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request http请求
     * @return
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 添加用户
     *
     * @param userAddRequest
     * @return
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 更新用户信息
     * @return 更新结果
     */
    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 编辑用户信息
     *
     * @param userEditRequest 编辑用户信息
     * @param loginUser       登录用户
     * @return 是否编辑成功
     */
    Boolean editUser(UserEditRequest userEditRequest, User loginUser);

    /**
     * 获取脱敏用户视图
     *
     * @param user 用户信息
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏用户视图列表
     *
     * @param userList 用户列表
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取mybatis-plus查询条件
     *
     * @param userQueryRequest 查询条件
     * @return
     */
    @Deprecated
    LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取用户分页信息
     *
     * @param userQueryRequest 查询条件
     * @return 户分页信息
     */
    Page<User> getUserPage(UserQueryRequest userQueryRequest);

    /**
     * 获取加密密码
     *
     * @param password 密码
     * @return 加密密码
     */
    String getEncryptPassword(String password);

    /**
     * 删除用户请求
     *
     * @param userDeleteRequest 删除用户请求
     * @return
     */
    Boolean deleteUser(UserDeleteRequest userDeleteRequest);

    /**
     * 修改密码
     *
     * @param passwordUpdateRequest 修改密码请求
     * @param request               http请求
     * @return 是否修改成功
     */
    Boolean updatePassword(PasswordUpdateRequest passwordUpdateRequest, HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request http请求
     * @return
     */
    Boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户信息
     * @return
     */
    Boolean isAdmin(User user);

    /**
     * 兑换会员
     *
     * @param vipExchangeRequest 兑换会员请求
     * @param loginUser          登录用户
     * @return 是否兑换成功
     */
    Boolean exchangeVip(VipExchangeRequest vipExchangeRequest, User loginUser);
}
