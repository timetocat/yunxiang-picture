package com.lyx.lopicture.utils.field;


import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

@Slf4j
public final class FieldUtils {


    private static final String WRITE_REPLACE = "writeReplace";

    public static <T, R> String getFieldName(FieldSupplier<T, R> supplier) {
        try {
            return getFieldName(getSerializedLambda(supplier));
        } catch (NoSuchMethodException | InvocationTargetException
                 | IllegalAccessException e) {
            log.error("getFieldName error", e);
            throw new RuntimeException(e);
        }
    }

    public static <T, R> String getFieldName(FieldConsumer<T, R> consumer) {
        try {
            return getFieldName(getSerializedLambda(consumer));
        } catch (NoSuchMethodException | InvocationTargetException
                 | IllegalAccessException e) {
            log.error("getFieldName error", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取序列化SerializedLambda
     *
     * @param supplier
     * @param <T>
     * @param <R>
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T, R> SerializedLambda getSerializedLambda(FieldSupplier<T, R> supplier)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = supplier.getClass().getDeclaredMethod(WRITE_REPLACE);
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(supplier);
    }

    /**
     * 获取序列化SerializedLambda
     *
     * @param consumer
     * @param <T>
     * @param <R>
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T, R> SerializedLambda getSerializedLambda(FieldConsumer<T, R> consumer)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = consumer.getClass().getDeclaredMethod(WRITE_REPLACE);
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(consumer);
    }

    /**
     * 获取字段名
     *
     * @param serializedLambda
     * @return
     */
    public static String getFieldName(SerializedLambda serializedLambda) {
        return methodToProperty(serializedLambda.getImplMethodName());
    }

    /**
     * 将方法名转换为属性名
     *
     * @param name
     * @return
     */
    private static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

}
