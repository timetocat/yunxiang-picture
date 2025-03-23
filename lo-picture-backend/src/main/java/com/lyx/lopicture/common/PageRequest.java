package com.lyx.lopicture.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.utils.SqlUtils;
import lombok.Data;

import java.util.List;

/**
 * 通用的分页请求类
 */
@Data
public class PageRequest implements Validator {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段对
     */
    private List<SortFieldPair> sortFieldPairs;

    @Override
    public void validate() {
        if (CollUtil.isEmpty(sortFieldPairs)) return;
        for (SortFieldPair sortFieldPair : sortFieldPairs) {
            sortFieldPair.setSortField(CharSequenceUtil
                    .toUnderlineCase(sortFieldPair.getSortField()));
            if (!SqlUtils.validSortField(sortFieldPair.getSortField())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序字段非法");
            }
        }
    }
}