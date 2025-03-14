package com.lyx.lopicture.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

public final class KeyGenerateUtils {

    public static String redisKey(Object obj) {
        String jsonStr = JSONUtil.toJsonStr(obj);
        return DigestUtil.md5Hex(jsonStr.getBytes());
    }
}
