package com.lyx.lopicture.model.enums.spacelevel;

public final class ProfessionalSpaceLevel extends SpaceLevel {

    public static final Integer MARK = 1;
    public static final String DESCRIPTION = "专业版";

    public ProfessionalSpaceLevel() {
        super(MARK, DESCRIPTION, 1000L, 1000L * ONE_MB);
    }

}
