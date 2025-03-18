package com.lyx.lopicture.model.vo.space.analyze;

import com.lyx.lopicture.model.enums.SpaceLevelEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 空间等级分析响应
 *
 * @param spaceLevel 空间等级
 * @param count      数量
 * @param size       大小
 */
public record SpaceLevelAnalyzeResponse(
        String spaceLevel,
        Long count,
        Long size
) implements Serializable {

    public record SpaceLevelAnalyzeInnerResponse(
            Integer spaceLevel,
            Long count,
            Long size
    ) implements Serializable {

        public SpaceLevelAnalyzeResponse transform() {
            return new SpaceLevelAnalyzeResponse(
                    SpaceLevelEnum.getSpaceLevelInfo(this.spaceLevel).getDescription(),
                    this.count,
                    this.size
            );
        }

        @Serial
        private static final long serialVersionUID = 1L;
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
