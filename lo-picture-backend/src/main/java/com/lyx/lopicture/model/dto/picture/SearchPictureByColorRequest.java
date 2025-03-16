package com.lyx.lopicture.model.dto.picture;

import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.utils.PictureUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 按照颜色搜索图片请求
 *
 * @param picColor 图片主色调
 * @param spaceId  空间id
 */
public record SearchPictureByColorRequest(
        String picColor,
        Long spaceId
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(!PictureUtils.isValidRGBHex(picColor), ErrorCode.PARAMS_ERROR);
    }
}
