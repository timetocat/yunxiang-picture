package com.lyx.lopicture.model.vo;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 图片标签分类列表视图
 *
 * @param tagList      标签列表
 * @param categoryList 分类列表
 */
@Builder
public record PictureTagCategory(
        List<String> tagList,
        List<String> categoryList
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
