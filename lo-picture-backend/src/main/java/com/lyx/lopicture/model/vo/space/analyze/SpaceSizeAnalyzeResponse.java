package com.lyx.lopicture.model.vo.space.analyze;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间图片大小分析响应
 *
 * @param sizeRange 图片大小范围
 * @param count     图片数量
 */
@Builder
public record SpaceSizeAnalyzeResponse(
        String sizeRange,
        Long count
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
