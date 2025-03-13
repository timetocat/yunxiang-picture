package com.lyx.lopicture.model.dto.picture;

import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.PictureReviewStatusEnum;

import java.io.Serial;
import java.io.Serializable;

public record PictureReviewRequest(
        Long id,
        Integer reviewStatus,
        String reviewMessage
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        PictureReviewStatusEnum pictureReviewStatusEnum = BaseValueEnum
                .getEnumByValue(PictureReviewStatusEnum.class, reviewStatus);
        ThrowUtils.throwIf(pictureReviewStatusEnum == null
                        || PictureReviewStatusEnum.REVIEWING.equals(pictureReviewStatusEnum),
                ErrorCode.PARAMS_ERROR, "审核状态错误");
    }
}
