package com.lyx.lopicture.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片
 *
 * @TableName picture
 */
@TableName(value = "picture", autoResultMap = true)
@Data
public class Picture implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 图片 url
     */
    @TableField(value = "url")
    private String url;

    /**
     * 缩略图 url
     */
    @TableField(value = "thumbnail_url")
    private String thumbnailUrl;

    /**
     * 图片名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 简介
     */
    @TableField(value = "introduction")
    private String introduction;

    /**
     * 分类
     */
    @TableField(value = "category")
    private String category;

    /**
     * 标签（JSON 数组）
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 图片体积
     */
    @TableField(value = "pic_size")
    private Long picSize;

    /**
     * 图片宽度
     */
    @TableField(value = "pic_width")
    private Integer picWidth;

    /**
     * 图片高度
     */
    @TableField(value = "pic_height")
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    @TableField(value = "pic_scale")
    private Double picScale;

    /**
     * 图片格式
     */
    @TableField(value = "pic_format")
    private String picFormat;

    /**
     * 创建用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 空间id
     */
    @TableField(value = "space_id")
    private Long spaceId;

    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    @TableField(value = "review_status")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    @TableField(value = "review_message")
    private String reviewMessage;

    /**
     * 审核人 ID
     */
    @TableField(value = "reviewer_id")
    private Long reviewerId;

    /**
     * 审核时间
     */
    @TableField(value = "review_time")
    private Date reviewTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 编辑时间
     */
    @TableField(value = "edit_time", updateStrategy = FieldStrategy.NOT_NULL)
    private Date editTime = new Date();

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