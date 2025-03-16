package com.lyx.lopicture.api.imagesearch;

import com.lyx.lopicture.api.imagesearch.model.ImageSearchResult;
import com.lyx.lopicture.api.imagesearch.sub.GetImageFirstUrlApi;
import com.lyx.lopicture.api.imagesearch.sub.GetImageListApi;
import com.lyx.lopicture.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl, boolean isIntranet) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl, isIntranet);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }

    public static void main(String[] args) {
        String url = "http://192.168.1.103:9000/lopic/public/1879878697389207553/2025-03-15_bxmK0R7xDIGwX4JF.webp";
        List<ImageSearchResult> imageList =
                searchImage(url, true);
        System.out.println("结果列表" + imageList);
    }
}
