package com.lyx.lopicture.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
 * @param id
 * @param userAccount 用户账户
 * @param userName    用户昵称
 * @param userAvatar  用户头像
 * @param userProfile 用户简介
 * @param userRole    用户角色
 * @param editTime    编辑时间
 * @param createTime  创建时间
 * @param updateTime  更新时间
 */
public record LoginUserVO(
        Long id,
        String userAccount,
        String userName,
        String userAvatar,
        String userProfile,
        String userRole,
        Date editTime,
        Date createTime,
        Date updateTime
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 3191241716373120793L;
}
