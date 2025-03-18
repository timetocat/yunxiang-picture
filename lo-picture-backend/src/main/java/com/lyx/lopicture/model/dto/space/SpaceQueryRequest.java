package com.lyx.lopicture.model.dto.space;

import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.PageRequest;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.SpaceLevelEnum;
import com.lyx.lopicture.model.enums.SpaceTypeEnum;
import com.lyx.lopicture.model.enums.spacelevel.SpaceLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询空间请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        super.validate();
        if (ObjectUtil.isNotNull(this.spaceLevel)) {
            SpaceLevel spaceLevel = SpaceLevelEnum.getSpaceLevelInfo(this.spaceLevel);
            ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间级别错误");
        }
        if (ObjectUtil.isNotNull(this.spaceType)) {
            SpaceTypeEnum spaceTypeEnum = BaseValueEnum.getEnumByValue(SpaceTypeEnum.class, spaceType);
            ThrowUtils.throwIf(spaceTypeEnum == null, ErrorCode.PARAMS_ERROR, "空间类型错误");
        }
    }
}