package com.lyx.lopicture.model.dto.picture;

import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 以图搜图请求
 *
 * @param pictureId 图片id
 */
public record SearchPictureByPictureRequest(
        Long pictureId
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(pictureId == null || pictureId <= 0, ErrorCode.PARAMS_ERROR);
    }
}
