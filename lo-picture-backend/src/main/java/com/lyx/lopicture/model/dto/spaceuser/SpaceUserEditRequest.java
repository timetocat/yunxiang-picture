package com.lyx.lopicture.model.dto.spaceuser;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.SpaceRoleEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 编辑空间成员请求
 *
 * @param id        主键id
 * @param spaceRole 空间角色：viewer/editor/admin
 */
public record SpaceUserEditRequest(
        Long id,
        String spaceRole
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        if (CharSequenceUtil.isBlank(spaceRole)) return;
        SpaceRoleEnum spaceRoleEnum = BaseValueEnum.getEnumByValue(SpaceRoleEnum.class, spaceRole);
        ThrowUtils.throwIf(spaceRoleEnum == null, ErrorCode.PARAMS_ERROR, "空间角色错误");
    }
}
