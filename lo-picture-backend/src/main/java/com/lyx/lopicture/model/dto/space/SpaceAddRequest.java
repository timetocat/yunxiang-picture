package com.lyx.lopicture.model.dto.space;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.SpaceLevelEnum;
import com.lyx.lopicture.model.enums.spacelevel.SpaceLevel;

import java.io.Serial;
import java.io.Serializable;

/**
 * 添加空间请求
 *
 * @param spaceName  空间名称
 * @param spaceLevel 空间级别：0-普通版 1-专业版 2-旗舰版
 */
public record SpaceAddRequest(
        String spaceName,
        Integer spaceLevel
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "空间名称不能为空");
        ThrowUtils.throwIf(ObjectUtil.isEmpty(this.spaceLevel), ErrorCode.PARAMS_ERROR, "空间级别不能为空");
        ThrowUtils.throwIf(spaceName.length() > 25, ErrorCode.PARAMS_ERROR, "空间名称过长");
        SpaceLevel spaceLevel = SpaceLevelEnum.getSpaceLevelInfo(this.spaceLevel);
        ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间级别错误");
    }
}
