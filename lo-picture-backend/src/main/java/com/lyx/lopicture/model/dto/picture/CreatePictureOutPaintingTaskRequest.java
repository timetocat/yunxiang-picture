package com.lyx.lopicture.model.dto.picture;

import com.lyx.lopicture.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建扩图任务请求
 *
 * @param pictureId  图片 id
 * @param parameters 扩图参数
 */
public record CreatePictureOutPaintingTaskRequest(
        Long pictureId,
        CreateOutPaintingTaskRequest.Parameters parameters
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(pictureId == null || pictureId <= 0, ErrorCode.PARAMS_ERROR);
    }
}
