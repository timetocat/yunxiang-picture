package com.lyx.lopicture.model.dto.space.analyze;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.PictureReviewStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceReviewAnalyzeRequest extends SpaceAnalyzeRequest {

    /**
     * 审核状态列表
     */
    private Set<Integer> reviewStatusList;

    /**
     * 时间维度：day / week / month
     */
    private String timeDimension;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        if (CollUtil.isNotEmpty(reviewStatusList)) {
            reviewStatusList = reviewStatusList.stream()
                    .filter(ObjectUtil::isNotNull)
                    .collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(reviewStatusList)) {
                reviewStatusList.forEach(reviewStatus -> ThrowUtils.throwIf(BaseValueEnum
                                .getEnumByValue(PictureReviewStatusEnum.class, reviewStatus) == null,
                        ErrorCode.PARAMS_ERROR, "审核状态错误"));
            }
        }
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(timeDimension), ErrorCode.PARAMS_ERROR, "时间维度不能为空");
        switch (timeDimension) {
            case "day":
            case "week":
            case "month":
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的时间维度");
        }
    }

}
