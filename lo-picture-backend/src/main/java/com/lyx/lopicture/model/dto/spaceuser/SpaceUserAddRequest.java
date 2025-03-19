package com.lyx.lopicture.model.dto.spaceuser;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.SpaceRoleEnum;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建空间成员请求
 *
 * @param spaceId   空间 ID
 * @param userId    用户 ID
 * @param spaceRole 空间角色：viewer/editor/admin
 */
@Builder
public record SpaceUserAddRequest(
        Long spaceId,
        Long userId,
        String spaceRole
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf((spaceId == null || spaceId <= 0) || (userId == null || userId <= 0),
                ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(spaceRole), ErrorCode.PARAMS_ERROR);
        SpaceRoleEnum spaceRoleEnum = BaseValueEnum.getEnumByValue(SpaceRoleEnum.class, spaceRole);
        ThrowUtils.throwIf(spaceRoleEnum == null, ErrorCode.PARAMS_ERROR, "空间角色错误");
    }
}
