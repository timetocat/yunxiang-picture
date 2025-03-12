package com.lyx.lopicture.utils;


import cn.hutool.core.text.CharSequenceUtil;


/**
 * SQL 工具
 */
public class SqlUtils {

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (CharSequenceUtil.isBlank(sortField)) {
            return false;
        }
        return !CharSequenceUtil.containsAny(sortField, "=", "(", ")", " ");
    }
}
