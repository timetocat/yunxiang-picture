package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

import static com.lyx.lopicture.exception.ErrorCode.PARAMS_ERROR;

/**
 * 用户注册请求
 *
 * @param email         邮箱
 * @param code          验证码
 * @param userAccount   用户账号
 * @param userPassword  用户密码
 * @param checkPassword 确认密码
 */
public record UserRegisterRequest(
        String email,
        String code,
        String userAccount,
        String userPassword,
        String checkPassword
) implements Serializable, Validator {
    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword, checkPassword, email, code),
                PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(!cn.hutool.core.lang.Validator.isEmail(email, true),
                PARAMS_ERROR, "邮箱格式错误");
        ThrowUtils.throwIf(userAccount.length() < 4, PARAMS_ERROR, "用户账号过短");
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, PARAMS_ERROR, "用户密码过短");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), PARAMS_ERROR, "两次输入的密码不一致");
    }
}
