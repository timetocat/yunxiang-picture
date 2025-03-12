package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

import static com.lyx.lopicture.exception.ErrorCode.PARAMS_ERROR;

/**
 * 用户登录请求
 *
 * @param userAccount  用户账号
 * @param userPassword 用户密码
 */
public record UserLoginRequest(
        String userAccount,
        String userPassword
) implements Serializable, Validator {
    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword), PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4, PARAMS_ERROR, "用户账号过短");
        ThrowUtils.throwIf(userPassword.length() < 8, PARAMS_ERROR, "用户密码过短");
    }
}
