package com.lyx.lopicture.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序字段对
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SortFieldPair {
    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = "ascend";
}
