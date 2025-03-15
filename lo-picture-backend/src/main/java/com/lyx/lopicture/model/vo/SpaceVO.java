package com.lyx.lopicture.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 空间视图
 *
 * @param id         主键id
 * @param spaceName  空间名称
 * @param spaceLevel 空间级别：0-普通版 1-专业版 2-旗舰版
 * @param maxSize    空间图片的最大总大小
 * @param maxCount   空间图片的最大数量
 * @param totalSize  当前空间下图片的总大小
 * @param totalCount 当前空间下的图片数量
 * @param userId     创建用户 id
 * @param createTime 创建时间
 * @param editTime   编辑时间
 * @param updateTime 更新时间
 * @param user       创建用户信息
 */
public record SpaceVO(
        Long id,
        String spaceName,
        Integer spaceLevel,
        Long maxSize,
        Long maxCount,
        Long totalSize,
        Long totalCount,
        Long userId,
        Date createTime,
        Date editTime,
        Date updateTime,
        UserVO user
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
