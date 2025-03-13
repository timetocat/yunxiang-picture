package com.lyx.lopicture.model.dto.picture;

import com.lyx.lopicture.common.Validator;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.exception.ThrowUtils;
import lombok.Builder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 批量上传图片请求
 *
 * @param searchText 搜索关键词
 * @param count      数量（默认为10）
 * @param namePrefix 图片名称前缀
 * @param category   分类
 * @param tags       标签
 */
@Builder
public record PictureUploadByBatchRequest(
        String searchText,
        Integer count,
        String namePrefix,
        String category,
        List<String> tags
) implements Serializable, Validator {

    public PictureUploadByBatchRequest {
        if (count == null) {
            count = 10;
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate() {
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "数量不能超过30");
    }
}
