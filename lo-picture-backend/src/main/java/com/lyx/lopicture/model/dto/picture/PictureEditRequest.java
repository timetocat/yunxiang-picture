package com.lyx.lopicture.model.dto.picture;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 图片编辑请求
 *
 * @param id           主键id
 * @param name         图片名称
 * @param introduction 简介
 * @param category     分类
 * @param tags         标签
 */
public record PictureEditRequest(
        Long id,
        String name,
        String introduction,
        String category,
        List<String> tags
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CharSequenceUtil.isNotBlank(name) && name.length() > 100,
                ErrorCode.PARAMS_ERROR, "图片名称过长");
        ThrowUtils.throwIf(CharSequenceUtil.isNotBlank(introduction) && introduction.length() > 1000,
                ErrorCode.PARAMS_ERROR, "图片简介过长");
    }
}
