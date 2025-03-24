package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱验证码请求
 *
 * @param email 邮箱地址
 * @param type  验证码用途：register-注册，resetPassword-重置密码，changeEmail-修改邮箱
 */
public record EmailCodeRequest(
        String email,
        String type
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(type, email), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(!cn.hutool.core.lang.Validator.isEmail(email, true),
                ErrorCode.PARAMS_ERROR, "邮箱格式错误");
    }
}
