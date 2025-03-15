package com.lyx.lopicture.model.enums.spacelevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public sealed class SpaceLevel
        permits CommonSpaceLevel, FlagshipSpaceLevel, ProfessionalSpaceLevel {

    protected static final long ONE_MB = 1024L * 1024L;

    private int value;

    private String description;

    private long maxCount;

    private long maxSize;

}
