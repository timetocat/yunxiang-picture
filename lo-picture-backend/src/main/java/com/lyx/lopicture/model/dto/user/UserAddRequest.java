package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

import static com.lyx.lopicture.exception.ErrorCode.PARAMS_ERROR;

/**
 * 用户添加请求（管理员）
 *
 * @param userName    用户名
 * @param userAccount 账户
 * @param userAvatar  头像
 * @param userProfile 用户简介
 * @param userRole    用户角色
 */
public record UserAddRequest(
        String userName,
        String userAccount,
        String userAvatar,
        String userProfile,
        String userRole
) implements Serializable, Validator {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(userAccount), PARAMS_ERROR, "用户账号不能为空");
        ThrowUtils.throwIf(userAccount.length() < 4, PARAMS_ERROR, "用户账号过短");
    }
}
