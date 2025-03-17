package com.lyx.lopicture.model.vo.space.analyze;

import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间资源使用分析响应类
 *
 * @param usedSize        已使用大小
 * @param maxSize         总大小
 * @param sizeUsageRatio  空间使用比例
 * @param usedCount       当前图片数量
 * @param maxCount        最大图片数量
 * @param countUsageRatio 图片数量占比
 */
@Builder
public record SpaceUsageAnalyzeResponse(
        Long usedSize,
        Long maxSize,
        Double sizeUsageRatio,
        Long usedCount,
        Long maxCount,
        Double countUsageRatio
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
