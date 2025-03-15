package com.lyx.lopicture.model.dto.space;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 编辑空间请求
 *
 * @param id        主键id
 * @param spaceName 空间名称
 */
public record SpaceEditRequest(
        Long id,
        String spaceName
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "空间名称不能为空");
        ThrowUtils.throwIf(spaceName.length() > 25, ErrorCode.PARAMS_ERROR, "空间名称过长");
    }
}
