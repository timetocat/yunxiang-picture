package com.lyx.lopicture.model.dto.picture;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @param pictureIdList 图片 id 列表
 * @param spaceId       空间 id
 * @param category      分类
 * @param tags          标签
 * @param nameRule      命名规则
 */
public record PictureEditByBatchRequest(
        List<Long> pictureIdList,
        Long spaceId,
        String category,
        List<String> tags,
        String nameRule
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        // ThrowUtils.throwIf(ObjectUtil.isNull(spaceId), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CollUtil.isEmpty(pictureIdList), ErrorCode.PARAMS_ERROR);
    }
}
