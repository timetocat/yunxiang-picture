package com.lyx.lopicture.model.dto.picture;

import com.lyx.lopicture.common.Validator;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片上传请求
 *
 * @param id      图片id（用于修改）
 * @param fileUrl 文件地址
 * @param picName 图片名称
 */
public record PictureUploadRequest(
        Long id,
        String fileUrl,
        String picName
) implements Serializable, Validator {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        // 例如 判断名称是否合规
    }
}
