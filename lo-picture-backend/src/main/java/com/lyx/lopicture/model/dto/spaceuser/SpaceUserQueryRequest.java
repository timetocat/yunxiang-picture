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
 * 空间用户查询请求
 *
 * @param id        主键id
 * @param spaceId   空间 ID
 * @param userId    用户 ID
 * @param spaceRole 空间角色：viewer/editor/admin
 */
@Builder
public record SpaceUserQueryRequest(
        Long id,
        Long spaceId,
        Long userId,
        String spaceRole
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        if (CharSequenceUtil.isBlank(spaceRole)) return;
        SpaceRoleEnum spaceRoleEnum = BaseValueEnum.getEnumByValue(SpaceRoleEnum.class, spaceRole);
        ThrowUtils.throwIf(spaceRoleEnum == null, ErrorCode.PARAMS_ERROR, "空间角色错误");
    }
}
