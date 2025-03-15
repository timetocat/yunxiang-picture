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
 * 更新空间请求
 *
 * @param id         主键id
 * @param spaceName  空间名称
 * @param spaceLevel 空间级别：0-普通版 1-专业版 2-旗舰版
 * @param maxSize    空间图片的最大总大小
 * @param maxCount   空间图片的最大数量
 */
public record SpaceUpdateRequest(
        Long id,
        String spaceName,
        Integer spaceLevel,
        Long maxSize,
        Long maxCount
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CharSequenceUtil.isNotBlank(spaceName) && spaceName.length() > 25,
                ErrorCode.PARAMS_ERROR, "空间名称过长");
        if (ObjectUtil.isEmpty(this.spaceLevel)) return;
        SpaceLevel spaceLevel = SpaceLevelEnum.getSpaceLevelInfo(this.spaceLevel);
        ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间级别错误");
    }
}
