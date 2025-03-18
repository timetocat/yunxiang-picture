package com.lyx.lopicture.model.dto.space.analyze;

import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.SpaceLevelEnum;
import com.lyx.lopicture.model.enums.spacelevel.SpaceLevel;

import java.io.Serial;
import java.io.Serializable;

public class SpaceLevelAnalyzeRequest implements Validator, Serializable {

    private Integer level;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        if (ObjectUtil.isNotNull(level)) {
            SpaceLevel spaceLevelInfo = SpaceLevelEnum.getSpaceLevelInfo(level);
            ThrowUtils.throwIf(spaceLevelInfo == null, ErrorCode.PARAMS_ERROR, "空间级别错误");
        }
    }
}
