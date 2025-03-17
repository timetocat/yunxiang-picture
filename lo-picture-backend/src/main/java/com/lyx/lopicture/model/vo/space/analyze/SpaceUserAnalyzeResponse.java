package com.lyx.lopicture.model.vo.space.analyze;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间用户上传行为分析响应
 *
 * @param period 时间区间
 * @param count  上传数量
 */
@Builder
public record SpaceUserAnalyzeResponse(
        String period,
        Long count
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}