package com.lyx.lopicture.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;

/**
 * Json 转换|处理 工具类
 */
public final class ObjectMapperUtils {

    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private ObjectMapperUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 处理 Long类型转换前端精度问题
     */
    public static String processLongAccuracy(Object object) throws JsonProcessingException {
        // 配置序列化：将 Long 类型转为 String，解决丢失精度问题
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance); // 支持 long 基本类型
        objectMapper.registerModule(module);
        // 序列化为 JSON 字符串
        return objectMapper.writeValueAsString(object);
    }

}
