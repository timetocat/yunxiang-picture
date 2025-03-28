package com.lyx.lopicture.model.vo;


import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 图片封装视图
 *
 * @param id             主键id
 * @param url            图片url
 * @param thumbnailUrl   缩略图url
 * @param name           图片名称
 * @param introduction   简介
 * @param tags           标签
 * @param category       分类
 * @param picSize        文件体积
 * @param picWidth       图片宽度
 * @param picHeight      图片高度
 * @param picScale       图片比例
 * @param picFormat      图片格式
 * @param picColor       图片主色调
 * @param userId         用户id
 * @param spaceId        空间id
 * @param createTime     创建时间
 * @param editTime       编辑时间
 * @param updateTime     更新时间
 * @param user           创建用户信息
 * @param permissionList 权限列表
 */
public record PictureVO(
        Long id,
        String url,
        String thumbnailUrl,
        String name,
        String introduction,
        List<String> tags,
        String category,
        Long picSize,
        Integer picWidth,
        Integer picHeight,
        Double picScale,
        String picFormat,
        String picColor,
        Long userId,
        Long spaceId,
        Date createTime,
        Date editTime,
        Date updateTime,
        UserVO user,
        List<String> permissionList
) implements Serializable {

    public PictureVO {
        permissionList = permissionList != null ? permissionList : Collections.emptyList();
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
