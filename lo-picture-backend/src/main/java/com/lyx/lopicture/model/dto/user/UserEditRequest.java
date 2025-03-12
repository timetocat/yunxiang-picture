package com.lyx.lopicture.model.dto.user;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 编辑请求
 *
 * @param userName    昵称
 * @param userAvatar  头像
 * @param userProfile 简介
 */
public record UserEditRequest(
        String userName,
        String userAvatar,
        String userProfile
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.isNotBlank(userName) && userName.length() > 50,
                ErrorCode.PARAMS_ERROR, "昵称过长");
    }
}
