package com.lyx.lopicture.model.vo.space.analyze;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间图片分类分析响应
 *
 * @param category  图片分类
 * @param count     图片数量
 * @param totalSize 分类图片总大小
 */
@Builder
public record SpaceCategoryAnalyzeResponse(
        String category,
        Long count,
        Long totalSize
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
