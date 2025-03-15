package com.lyx.lopicture.model.enums.spacelevel;

public final class FlagshipSpaceLevel extends SpaceLevel {

    public static final Integer MARK = 2;
    public static final String DESCRIPTION = "旗舰版";

    public FlagshipSpaceLevel() {
        super(MARK, DESCRIPTION, 10000L, 10000L * ONE_MB);
    }

}
