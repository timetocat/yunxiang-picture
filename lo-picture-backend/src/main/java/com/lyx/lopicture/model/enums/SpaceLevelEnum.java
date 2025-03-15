package com.lyx.lopicture.model.enums;

import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.model.enums.spacelevel.CommonSpaceLevel;
import com.lyx.lopicture.model.enums.spacelevel.FlagshipSpaceLevel;
import com.lyx.lopicture.model.enums.spacelevel.ProfessionalSpaceLevel;
import com.lyx.lopicture.model.enums.spacelevel.SpaceLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum SpaceLevelEnum implements BaseValueEnum<SpaceLevel> {

    COMMON(CommonSpaceLevel.DESCRIPTION, new CommonSpaceLevel()),
    PROFESSIONAL(ProfessionalSpaceLevel.DESCRIPTION, new ProfessionalSpaceLevel()),
    FLAGSHIP(FlagshipSpaceLevel.DESCRIPTION, new FlagshipSpaceLevel());

    public static final Class<SpaceLevel> RETURN_TYPE = SpaceLevel.class;

    private final String text;
    private final SpaceLevel value;

    SpaceLevelEnum(String text, SpaceLevel value) {
        this.text = text;
        this.value = value;
    }

    private static final Map<Integer, SpaceLevel> SPACE_LEVEL_MAP =
            new HashMap<>(SpaceLevel.class.getPermittedSubclasses().length);

    static {
        SPACE_LEVEL_MAP.put(CommonSpaceLevel.MARK, new CommonSpaceLevel());
        SPACE_LEVEL_MAP.put(ProfessionalSpaceLevel.MARK, new ProfessionalSpaceLevel());
        SPACE_LEVEL_MAP.put(FlagshipSpaceLevel.MARK, new FlagshipSpaceLevel());
    }

    public static SpaceLevel getSpaceLevelInfo(int value) {
        return SPACE_LEVEL_MAP.get(value);
    }
}
