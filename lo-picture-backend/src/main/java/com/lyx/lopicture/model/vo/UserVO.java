package com.lyx.lopicture.model.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @param id
 * @param userAccount 用户账户
 * @param userName    用户昵称
 * @param userAvatar  用户头像
 * @param userProfile 用户简介
 * @param userRole    用户角色
 * @param createTime  创建时间
 */
public record UserVO(
        Long id,
        String userAccount,
        String userName,
        String userAvatar,
        String userProfile,
        String userRole,
        Date createTime
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
