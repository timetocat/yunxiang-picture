package com.lyx.lopicture.mapper;

import com.lyx.lopicture.model.dto.space.analyze.SpaceCategoryAnalyzeRequest;
import com.lyx.lopicture.model.dto.space.analyze.SpaceUserAnalyzeRequest;
import com.lyx.lopicture.model.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.lyx.lopicture.model.vo.space.analyze.SpaceUserAnalyzeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PictureMapperTest {

    @Resource
    private PictureMapper pictureMapper;

    @Test
    void getAnalyzeGroupByCategory() {
        SpaceCategoryAnalyzeRequest request = new SpaceCategoryAnalyzeRequest();
        request.setSpaceId(1900860543316832258L);
        List<SpaceCategoryAnalyzeResponse> analyzeResponseList = pictureMapper.getAnalyzeGroupByCategory(request);
        for (SpaceCategoryAnalyzeResponse analyzeResponse : analyzeResponseList) {
            System.out.println(analyzeResponse);
        }
    }

    @Test
    void getAnalyzeGroupByUser() {
        SpaceUserAnalyzeRequest request = new SpaceUserAnalyzeRequest();
        request.setTimeDimension("day");
        request.setUserId(1L);
        List<SpaceUserAnalyzeResponse> analyzeResponseList = pictureMapper.getAnalyzeGroupByUser(request);
        for (SpaceUserAnalyzeResponse analyzeResponse : analyzeResponseList) {
            System.out.println(analyzeResponse);
        }
    }
}