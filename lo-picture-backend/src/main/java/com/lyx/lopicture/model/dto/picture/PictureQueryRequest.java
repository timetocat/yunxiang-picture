package com.lyx.lopicture.model.dto.picture;

import cn.hutool.core.util.ObjectUtil;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.common.PageRequest;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import com.lyx.lopicture.model.enums.PictureReviewStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 搜索词（同时搜名称、简介等）
     */
    private String searchText;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 是否只查询 spaceId 为 null 的数据
     */
    private boolean nullSpaceId;

    /**
     * 审核状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 ID
     */
    private Long reviewerId;

    /**
     * 审核时间开始
     */
    private Date reviewTimeStart;

    /**
     * 审核时间结束
     */
    private Date reviewTimeEnd;

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        super.validate();
        if (ObjectUtil.isNotEmpty(reviewStatus)) {
            PictureReviewStatusEnum pictureReviewStatusEnum = BaseValueEnum
                    .getEnumByValue(PictureReviewStatusEnum.class, reviewStatus);
            ThrowUtils.throwIf(pictureReviewStatusEnum == null, ErrorCode.PARAMS_ERROR, "审核状态错误");
        }
    }
}