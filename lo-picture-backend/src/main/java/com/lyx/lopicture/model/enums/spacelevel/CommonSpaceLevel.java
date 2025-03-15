package com.lyx.lopicture.model.enums.spacelevel;

public final class CommonSpaceLevel extends SpaceLevel {

    public static final Integer MARK = 0;
    public static final String DESCRIPTION = "普通版";

    public CommonSpaceLevel() {
        super(MARK, DESCRIPTION, 100L, 100L * ONE_MB);
    }

}
