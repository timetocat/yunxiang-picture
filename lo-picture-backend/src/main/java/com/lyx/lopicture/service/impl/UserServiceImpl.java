package com.lyx.lopicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.mapper.UserMapper;
import com.lyx.lopicture.model.convert.UserConvert;
import com.lyx.lopicture.model.dto.user.*;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.UserRoleEnum;
import com.lyx.lopicture.model.vo.LoginUserVO;
import com.lyx.lopicture.model.vo.UserVO;
import com.lyx.lopicture.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-01-13 17:04:57
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    private static final UserConvert USER_CONVERT = UserConvert.INSTANCE;

    // 抛出异常
    private static final Boolean IS_THROW = Boolean.TRUE;
    // 不抛出异常
    private static final Boolean IS_NOT_THROW = Boolean.FALSE;

    @Value("${default.username:无名}")
    private String defaultUsername;

    @Value("${default.password:12345678}")
    private String defaultPassword;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新用户id
     */
    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.userAccount();
        String userPassword = userRegisterRequest.userPassword();
        // 检查是否重复账户
        Long count = this.baseMapper.selectCount(Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userAccount));
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账户已存在");
        // 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(defaultUsername);
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误!");
        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          http请求
     * @return 已登录用户视图
     */
    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 加密
        String encryptPassword = getEncryptPassword(userLoginRequest.userPassword());
        User user = this.baseMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userLoginRequest.userAccount())
                .eq(User::getUserPassword, encryptPassword));
        if (user == null) {
            log.error("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名或密码错误");
        }
        // 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request http请求
     * @return 未脱敏用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 判断是否已经登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser == null || currentUser.getId() == null,
                ErrorCode.NOT_LOGIN_ERROR);
        // 数据库再次查询
        currentUser = this.getById(currentUser.getId());
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return currentUser;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request http请求
     * @return 未脱敏用户信息
     */
    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        Long id = currentUser.getId();
        if (!checkUserExist(id, IS_NOT_THROW)) {
            return null;
        }
        return this.getById(id);
    }

    /**
     * 获取当前登录用户视图
     *
     * @param user 用户信息
     * @return 用户视图
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        return USER_CONVERT.mapToLoginUserVO(user);
    }

    /**
     * 用户注销
     *
     * @param request http请求
     * @return
     */
    @Override
    public Boolean userLogout(HttpServletRequest request) {
        // 判断用户是否登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(userObj == null, ErrorCode.NOT_LOGIN_ERROR);
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加请求
     * @return
     */
    @Override
    public Long addUser(UserAddRequest userAddRequest) {
        String userAccount = userAddRequest.userAccount();
        long count = count(Wrappers.lambdaQuery(User.class).eq(User::getUserAccount, userAccount));
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账户已存在");
        User user = USER_CONVERT.mapToUser(userAddRequest);
        String encryptPassword = getEncryptPassword(defaultPassword);
        user.setUserPassword(encryptPassword);
        if (CharSequenceUtil.isBlank(user.getUserName())) {
            user.setUserName(defaultUsername);
        }
        boolean result = this.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return user.getId();
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 更新用户信息
     * @return 更新结果
     */
    @Override
    public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
        checkUserExist(userUpdateRequest.id(), IS_THROW);
        User user = USER_CONVERT.mapToUser(userUpdateRequest);
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 编辑用户信息
     *
     * @param userEditRequest 编辑用户信息
     * @param loginUser       登录用户
     * @return 是否编辑成功
     */
    @Override
    public Boolean editUser(UserEditRequest userEditRequest, User loginUser) {
        User user = USER_CONVERT.mapToUser(userEditRequest);
        user.setId(loginUser.getId());
        boolean result = this.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 获取脱敏用户视图
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        return USER_CONVERT.mapToUserVO(user);
    }

    /**
     * 获取脱敏用户视图列表
     *
     * @param userList 用户列表
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取mybatis-plus查询条件
     *
     * @param userQueryRequest 查询条件
     * @return
     */
    @Deprecated
    @Override
    public LambdaQueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class);
        if (userQueryRequest != null) {
            Long id = userQueryRequest.getId();
            queryWrapper.eq(ObjUtil.isNotNull(id), User::getId, id);
            String userRole = userQueryRequest.getUserRole();
            queryWrapper.eq(CharSequenceUtil.isNotBlank(userRole), User::getUserRole, userRole);
            String userAccount = userQueryRequest.getUserAccount();
            queryWrapper.like(CharSequenceUtil.isNotBlank(userAccount), User::getUserAccount, userAccount);
            String userName = userQueryRequest.getUserName();
            queryWrapper.like(CharSequenceUtil.isNotBlank(userName), User::getUserName, userName);
            String userProfile = userQueryRequest.getUserProfile();
            queryWrapper.like(CharSequenceUtil.isNotBlank(userProfile), User::getUserProfile, userProfile);
            /*String sortField = userQueryRequest.getSortFieldPairs().get(0).getSortField();
            String sortOrder = userQueryRequest.getSortFieldPairs().get(0).getSortOrder();
            queryWrapper.apply(SqlUtils.validSortField(sortField), "ORDER BY {} {}", sortField,
                    CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? "ASC" : "DESC");*/
        }
        return queryWrapper;
    }

    /**
     * 获取用户分页信息
     *
     * @param userQueryRequest 查询条件
     * @return 户分页信息
     */
    @Override
    public Page<User> getUserPage(UserQueryRequest userQueryRequest) {
        Page<User> page = new Page<>(userQueryRequest.getCurrent(), userQueryRequest.getPageSize());
        return this.baseMapper.selectPage(page, userQueryRequest);
    }

    @Override
    public String getEncryptPassword(String password) {
        // 盐值，混淆密码
        final String SATL = "lyx";
        return DigestUtils.md5DigestAsHex((SATL + password).getBytes());
    }

    @Override
    public Boolean deleteUser(UserDeleteRequest userDeleteRequest) {
        checkUserExist(userDeleteRequest.getId(), IS_THROW);
        boolean result = this.removeById(userDeleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 修改密码
     *
     * @param passwordUpdateRequest 修改密码请求
     * @param request               http请求
     * @return 是否修改成功
     */
    @Override
    public Boolean updatePassword(PasswordUpdateRequest passwordUpdateRequest, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        Long userId = loginUser.getId();
        // 校验旧密码
        String oldPassword = this.getEncryptPassword(passwordUpdateRequest.oldPassword());
        boolean exists = exists(Wrappers.lambdaQuery(User.class)
                .eq(User::getId, userId)
                .eq(User::getUserPassword, oldPassword));
        ThrowUtils.throwIf(!exists, ErrorCode.PARAMS_ERROR, "旧密码错误");
        User newUser = new User();
        String newPassword = this.getEncryptPassword(passwordUpdateRequest.newPassword());
        newUser.setId(userId);
        newUser.setUserPassword(newPassword);
        boolean result = this.updateById(newUser);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR);
        this.userLogout(request);
        return true;
    }

    /**
     * 是否为管理员
     *
     * @param request http请求
     * @return
     */
    @Override
    public Boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return isAdmin((User) userObj);
    }

    /**
     * 是否为管理员
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public Boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        Long id = user.getId();
        checkUserExist(id, IS_THROW);
        // 查询用户权限
        user = this.getOne(Wrappers.lambdaQuery(User.class)
                .select(User::getUserRole)
                .eq(User::getId, id));
        return UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 判断用户是否存在
     *
     * @param id      主键id
     * @param throwEx 是否抛出异常
     */
    public boolean checkUserExist(Long id, Boolean throwEx) {
        // 判断用户是否存在
        boolean exists = exists(Wrappers.lambdaQuery(User.class).eq(User::getId, id));
        if (!exists) {
            if (!throwEx) return false;
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        return true;
    }

}




