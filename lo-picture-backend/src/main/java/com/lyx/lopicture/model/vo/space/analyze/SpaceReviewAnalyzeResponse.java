package com.lyx.lopicture.model.vo.space.analyze;

import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.model.enums.PictureReviewStatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片空间审核分析
 *
 * @param period       时间区间
 * @param reviewStatus 审核状态
 * @param count        审核数量
 */
public record SpaceReviewAnalyzeResponse(
        String period,
        String reviewStatus,
        Long count
) implements Serializable {

    public record SpaceReviewAnalyzeInnerResponse(
            String period,
            Integer reviewStatus,
            Long count
    ) implements Serializable {

        public SpaceReviewAnalyzeResponse transform() {
            return new SpaceReviewAnalyzeResponse(
                    this.period,
                    BaseValueEnum.getEnumByValue(PictureReviewStatusEnum.class, this.reviewStatus).getText(),
                    this.count
            );
        }

        @Serial
        private static final long serialVersionUID = 1L;
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
