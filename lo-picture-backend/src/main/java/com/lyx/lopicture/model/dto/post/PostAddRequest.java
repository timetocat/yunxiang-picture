package com.lyx.lopicture.model.dto.post;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 帖子添加请求
 *
 * @param title   标题
 * @param content 内容
 * @param tags    标签列表
 */
public record PostAddRequest(
        String title,
        String content,
        List<String> tags

) implements Serializable, Validator {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(!CharSequenceUtil.isAllNotBlank(title, content), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CollUtil.isEmpty(tags), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isNotBlank(title) && title.length() > 80,
                ErrorCode.PARAMS_ERROR, "标题过长");
        ThrowUtils.throwIf(StringUtils.isNotBlank(content) && content.length() > 8192,
                ErrorCode.PARAMS_ERROR, "内容过长");
    }
}
