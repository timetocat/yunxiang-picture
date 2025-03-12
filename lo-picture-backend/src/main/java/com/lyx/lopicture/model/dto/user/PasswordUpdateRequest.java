package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

import static com.lyx.lopicture.exception.ErrorCode.PARAMS_ERROR;

/**
 * 更新用户密码请求
 *
 * @param oldPassword   旧密码
 * @param newPassword   新密码
 * @param checkPassword 确认密码
 */
public record PasswordUpdateRequest(
        String oldPassword,
        String newPassword,
        String checkPassword
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(oldPassword, newPassword, checkPassword),
                PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(oldPassword.length() < 8 || newPassword.length() < 8 || checkPassword.length() < 8,
                PARAMS_ERROR, "密码过短");
        ThrowUtils.throwIf(!newPassword.equals(checkPassword), PARAMS_ERROR, "两次输入的密码不一致");
    }
}
