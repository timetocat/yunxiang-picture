package com.lyx.lopicture.utils;

import cn.hutool.core.text.CharSequenceUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class PictureUtils {

    private PictureUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 获取图片主色
     *
     * @param image 图片
     * @return
     * @throws IOException
     */
    public static String getRGB(BufferedImage image) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        Map<Integer, Integer> colorCount = new HashMap<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                colorCount.put(rgb, colorCount.getOrDefault(rgb, 0) + 1);
            }
        }

        // 找到出现次数最多的颜色
        int mainColor = colorCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();

        // 格式化为 0xRRGGBB 格式
        return String.format("0x%06X", mainColor & 0xFFFFFF);
    }

    public static boolean isValidRGBHex(String hex) {
        // 匹配 #RRGGBB、0xRRGGBB 或 RRGGBB（可带可无前缀，不区分大小写）
        String regex = "^(#|0x)?([0-9A-Fa-f]{6})$";
        return CharSequenceUtil.isNotBlank(hex) && hex.matches(regex);
    }


    /**
     * 计算两个颜色的相似度
     *
     * @param color1 第一个颜色
     * @param color2 第二个颜色
     * @return 相似度（0到1之间，1为完全相同）
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        // 计算欧氏距离
        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));

        // 计算相似度
        return 1 - distance / Math.sqrt(3 * Math.pow(255, 2));
    }

    /**
     * 根据十六进制颜色代码计算相似度
     *
     * @param hexColor1 第一个颜色的十六进制代码（如 0xFF0000）
     * @param hexColor2 第二个颜色的十六进制代码（如 0xFE0101）
     * @return 相似度（0到1之间，1为完全相同）
     */
    public static double calculateSimilarity(String hexColor1, String hexColor2) {
        Color color1 = Color.decode(hexColor1);
        Color color2 = Color.decode(hexColor2);
        return calculateSimilarity(color1, color2);
    }

}
