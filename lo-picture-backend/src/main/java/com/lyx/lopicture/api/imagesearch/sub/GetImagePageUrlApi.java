package com.lyx.lopicture.api.imagesearch.sub;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面地址（step 1）
 */
@Slf4j
public class GetImagePageUrlApi {


    /**
     * 获取以图搜图页面地址
     *
     * @param imageUrl
     * @param isIntranet 是否是内网
     * @return
     */
    public static String getImagePageUrl(String imageUrl, boolean isIntranet) {
        // image: https%3A%2F%2Fwww.codefather.cn%2Flogo.png
        //tn: pc
        //from: pc
        //image_source: PC_UPLOAD_URL
        //sdkParams:
        // 内网
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        if (!isIntranet) {
            formData.put("image", imageUrl);
        }
        formData.put("tn", "pc");
        formData.put("from", "pc");
        // formData.put("image_source", "PC_UPLOAD_URL");
        formData.put("image_source", "PC_UPLOAD_FILE");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        try {
            // 2. 发送请求
            HttpRequest httpRequest = HttpRequest.post(url)
                    .header("acs-token", RandomUtil.randomString(1))
                    .form(formData)
                    .timeout(5000);
            if (isIntranet) {
                byte[] bytes = HttpRequest.get(imageUrl).execute().bodyBytes();
                httpRequest.form("image", bytes, "image.png");
            }
            HttpResponse httpResponse = httpRequest.execute();

            if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            // {"status":0,"msg":"Success","data":{"url":"https://graph.baidu.com/sc","sign":"1262fe97cd54acd88139901734784257"}}
            String body = httpResponse.body();
            Map<String, Object> result = JSONUtil.toBean(body, Map.class);
            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            // 对 URL 进行解码
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (StrUtil.isBlank(searchResultUrl)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效的结果地址");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("调用百度以图搜图接口失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
//        String imageUrl = "https://www.codefather.cn/logo.png";
        String imageUrl = "http://192.168.1.103:9000/lopic/public/1/2025-03-15_7Q0qm1uFWnZNYLut_thumbnail.jpg";
        String searchResultUrl = getImagePageUrl(imageUrl, true);
        System.out.println("搜索成功，结果 URL：" + searchResultUrl);
    }
}
