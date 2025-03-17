package com.lyx.lopicture.model.dto.space.analyze;

import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空间图片分类分析请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceCategoryAnalyzeRequest extends SpaceAnalyzeRequest {

    @Override
    public void validate() {
        if (isQueryAll()) return;
        // 公共图库
        if (isQueryPublic()) return;
        if (ObjectUtil.isNotNull(getSpaceId())) return;
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }
}
