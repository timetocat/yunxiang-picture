package com.lyx.lopicture.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lyx.lopicture.model.enums.UserRoleEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 密码
     */
    @TableField(value = "user_password")
    private String userPassword;

    /**
     * 用户昵称
     */
    @TableField(value = "user_name", insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String userName;

    /**
     * 用户头像
     */
    @TableField(value = "user_avatar", insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String userAvatar;

    /**
     * 用户简介
     */
    @TableField(value = "user_profile", insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @TableField(value = "user_role", insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String userRole = UserRoleEnum.USER.getValue();

    /**
     * 会员过期时间
     */
    @TableField(value = "vip_expire_time")
    private Date vipExpireTime;

    /**
     * 会员兑换码
     */
    @TableField(value = "vip_code")
    private String vipCode;

    /**
     * 会员编号
     */
    @TableField(value = "vip_number")
    private Long vipNumber;

    /**
     * 编辑时间
     */
    @TableField(value = "edit_time")
    private Date editTime = new Date();

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}