package com.lyx.lopicture.model.dto.user;

import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

import static com.lyx.lopicture.exception.ErrorCode.PARAMS_ERROR;

/**
 * 更新请求
 *
 * @param id          用户id
 * @param userName    用户昵称
 * @param userAvatar  用户头像
 * @param userProfile 用户简介
 * @param userRole    用户角色
 */
public record UserUpdateRequest(
        Long id,
        String userName,
        String userAvatar,
        String userProfile,
        String userRole
) implements Serializable, Validator {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null, PARAMS_ERROR);
    }
}
