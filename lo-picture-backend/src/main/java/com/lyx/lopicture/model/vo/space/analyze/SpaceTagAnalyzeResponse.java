package com.lyx.lopicture.model.vo.space.analyze;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间图片标签分析响应
 *
 * @param tag   图片标签
 * @param count 使用次数
 */
@Builder
public record SpaceTagAnalyzeResponse(
        String tag,
        Long count
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
