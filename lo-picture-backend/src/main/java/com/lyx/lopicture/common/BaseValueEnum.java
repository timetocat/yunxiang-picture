package com.lyx.lopicture.common;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 枚举通用接口
 */
public interface BaseValueEnum<T> {
    /**
     * 获取value
     *
     * @return value
     */
    T getValue();

    /**
     * 获取text
     *
     * @return text
     */
    String getText();

    /**
     * @param enumClass  枚举类Class对象
     * @param returnType 返回类型
     * @param <U>        枚举类的类型，必须实现BaseValueEnum接口
     * @param <R>        返回类型
     * @return 获取枚举值列表
     */
    static <U extends Enum<U> & BaseValueEnum<?>, R> List<R> getValues(Class<U> enumClass, Class<R> returnType) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(BaseValueEnum::getValue)
                .map(returnType::cast)
                .collect(Collectors.toList());
    }

    /**
     * 根据value获取枚举
     * @param enumClass 枚举类Class对象
     * @param value 枚举值
     * @return 枚举类型
     * @param <U> 枚举类的类型，必须实现BaseValueEnum接口
     */
    static <U extends Enum<U> & BaseValueEnum<?>> U getEnumByValue(Class<U> enumClass, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (U enumValue : enumClass.getEnumConstants()) {
            if (Objects.equals(enumValue.getValue(), value)) {
                return enumValue;
            }
        }
        return null;
    }
}
