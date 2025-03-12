package com.lyx.lopicture.controller;

import cn.hutool.core.text.CharSequenceUtil;
import com.lyx.lopicture.annotation.AuthCheck;
import com.lyx.lopicture.common.BaseResponse;
import com.lyx.lopicture.common.ResultUtils;
import com.lyx.lopicture.constant.UserConstant;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.manager.osManager.OsManager;
import com.lyx.lopicture.manager.upload.picture.PictureUpload;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private OsManager osManager;

    @Resource(name = "filePictureUpload")
    private PictureUpload pictureUpload;

    /**
     * 测试文件上传
     *
     * @param multipartFile
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        try {
            // 上传文件
            osManager.putObject(filepath, multipartFile);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) {
        try {
            byte[] bytes = osManager.getObject(filepath);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("test/upload/picture")
    public BaseResponse<UploadPictureResult> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                                           @RequestParam(value = "uploadPathPrefix", required = false) String uploadPathPrefix) {

        if (CharSequenceUtil.isBlank(uploadPathPrefix)) {
            uploadPathPrefix = "test";
        }
        // 上传文件
        UploadPictureResult uploadPictureResult = pictureUpload.uploadPicture(multipartFile, uploadPathPrefix);
        return ResultUtils.success(uploadPictureResult);
    }

}
