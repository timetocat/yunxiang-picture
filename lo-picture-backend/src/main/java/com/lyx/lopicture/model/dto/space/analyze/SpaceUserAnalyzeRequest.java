package com.lyx.lopicture.model.dto.space.analyze;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空间用户上传行为分析请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 时间维度：day / week / month
     */
    private String timeDimension;

    @Override
    public void validate() {
        ThrowUtils.throwIf(CharSequenceUtil.isBlank(timeDimension), ErrorCode.PARAMS_ERROR);
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