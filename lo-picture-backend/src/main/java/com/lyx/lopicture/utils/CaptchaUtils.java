package com.lyx.lopicture.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.digest.DigestUtil;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CaptchaUtils {


    /**
     * 生成验证码
     */
    public static Map<String, String> getCaptcha() {
        // 仅包含数字的字符集
        String characters = "0123456789";
        // 生成 4 位数字验证码
        RandomGenerator randomGenerator = new RandomGenerator(characters, 4);
        // 定义图片的显示大小，并创建验证码对象
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(320, 100, 4, 4);
        shearCaptcha.setGenerator(randomGenerator);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        shearCaptcha.write(outputStream);
        byte[] captchaBytes = outputStream.toByteArray();
        String base64Captcha = Base64.getEncoder().encodeToString(captchaBytes);
        String captchaCode = shearCaptcha.getCode();

        // 使用 Hutool 的 MD5 加密
        String encryptedCaptcha = DigestUtil.md5Hex(captchaCode);

        Map<String, String> data = new HashMap<>();
        data.put("base64Captcha", base64Captcha);
        data.put("encryptedCaptcha", encryptedCaptcha);
        return data;
    }

    /**
     * 校验验证码
     */
    public static boolean checkCaptcha(String captchaCode, String encryptedCaptcha) {
        // 使用 Hutool 的 MD5 加密
        String encryptedInputCaptcha = DigestUtil.md5Hex(captchaCode);
        return CharSequenceUtil.equals(encryptedCaptcha, encryptedInputCaptcha);
    }

}
