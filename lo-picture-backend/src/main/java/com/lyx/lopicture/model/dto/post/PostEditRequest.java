package com.lyx.lopicture.model.dto.post;

import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求（用户）
 *
 * @param id      主键id
 * @param title   标题
 * @param content 内容
 * @param tags    标签列表
 */
public record PostEditRequest(
        Long id,
        String title,
        String content,
        List<String> tags
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isNotBlank(title) && title.length() > 80,
                ErrorCode.PARAMS_ERROR, "标题过长");
        ThrowUtils.throwIf(StringUtils.isNotBlank(content) && content.length() > 8192,
                ErrorCode.PARAMS_ERROR, "内容过长");
    }
}
