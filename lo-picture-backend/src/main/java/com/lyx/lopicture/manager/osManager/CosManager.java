package com.lyx.lopicture.manager.osManager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import com.lyx.lopicture.config.CosClientConfig;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.manager.osManager.operator.PutOperator;
import com.lyx.lopicture.manager.osManager.operator.TypeOperator;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@ConditionalOnBean(COSClient.class)
@ConditionalOnProperty(name = "default.enable-os", havingValue = "true")
public class CosManager implements OsManager {

    @Resource
    private COSClient cosClient;

    @Resource
    private CosClientConfig cosClientConfig;

    // 上传对象
    private final PutOperator<File, PutObjectResult> putOperator = (key, file) -> {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    };

    // 上传对象（附带图片信息）
    private final PutOperator<File, PutObjectResult> putPictureOperator = (key, file) -> {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种图片的处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 图片处理规则列表
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 1. 图片压缩（转成 webp 格式）
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setFileId(webpKey);
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setRule("imageMogr2/format/webp");
        rules.add(compressRule);
        // 2. 缩略图处理，仅对 > 20 KB 的图片生成缩略图
        if (file.length() > USE_THUMBNAIL_SIZE) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            // 拼接缩略图的路径
            String thumbnailKey = getThumbnailKey(key);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
            rules.add(thumbnailRule);
        }
        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    };

    // 处理上传类型
    private final TypeOperator<File> typeOperator = (key, multipartFile) -> {
        try {
            File file = File.createTempFile(key, null);
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    };


    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param t   上传对象
     * @return 操作结果
     */
    @Override
    public <T, R> R putObject(String key, T t) {
        File file = null;
        try {
            if (t instanceof File) {
                file = (File) t;
                return (R) putOperator.put(key, file);
            }
            file = processType(t, key);
            return (R) putOperator.put(key, file);
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 上传对象（附带图片信息）
     *
     * @param key 唯一键
     * @param t   上传对象
     * @return 操作结果
     */
    @Override
    public <T, R> R putPictureObject(String key, T t) {
        if (t instanceof File) {
            return (R) putPictureOperator.put(key, (File) t);
        }
        return (R) putPictureOperator.put(key, processType(t, key));
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     * @return 对象字节数组
     */
    @Override
    public byte[] getObject(String key) {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosClient.getObject(cosClientConfig.getBucket(), key);
            cosObjectInput = cosObject.getObjectContent();
            return IOUtils.toByteArray(cosObjectInput);
        } catch (Exception e) {
            log.error("file download error, filepath = " + key, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                try {
                    cosObjectInput.close();
                } catch (IOException e) {
                    log.error("Failed to close COSObjectInputStream", e);
                }
            }
        }
    }

    /**
     * 获取图片地址
     *
     * @param key 唯一键
     * @return 图片地址
     */
    @Override
    public String getPictureUrl(String key) {
        if (!cosClient.doesObjectExist(cosClientConfig.getBucket(), key)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }
        return cosClient.getObjectUrl(cosClientConfig.getBucket(), key).toString();
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    @Override
    public void deleteObject(String key) {
        String prefix = cosClientConfig.getHost() + "/";
        cosClient.deleteObject(cosClientConfig.getBucket(),
                !key.startsWith(prefix) ? key : CharSequenceUtil.removePrefix(key, prefix));
    }

    /**
     * 上传图片
     *
     * @param inputSource      上传对象
     * @param key              唯一键
     * @param originalFilename 文件名
     * @return 上传结果
     */
    @Override
    public UploadPictureResult uploadPicture(Object inputSource, String key, String originalFilename) {
        File file = null;
        try {
            PutObjectResult putObjectResult = putPictureObject(key, inputSource);
            // 获取图片信息对象，封装返回结果
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 获取到图片处理结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                // 获取压缩之后得到的文件信息
                CIObject compressedCiObject = objectList.get(0);
                // 缩略图默认等于压缩图
                CIObject thumbnailCiObject = compressedCiObject;
                // 有生成缩略图，才获取缩略图
                if (objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1);
                }
                // 封装压缩图的返回结果
                return buildResult(originalFilename, compressedCiObject, thumbnailCiObject);
            }
            file = processType(inputSource, key);
            return buildResult(originalFilename, file, key, imageInfo);
        } finally {
            this.deleteTempFile(file);
            if (inputSource instanceof File) {
                this.deleteTempFile((File) inputSource);
            }
        }
    }

    /**
     * 处理上传类型
     *
     * @param t 上传对象
     * @return
     */
    @Override
    public <T, R> R processType(T t, String key) {
        if (t instanceof String) {
            return (R) processType((String) t, key);
        }
        if (t instanceof MultipartFile) {
            return (R) typeOperator.operate(key, (MultipartFile) t);
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    /**
     * 封装返回结果
     *
     * @param originalFilename   原始文件名
     * @param compressedCiObject 压缩后的对象
     * @param thumbnailCiObject  缩略图对象
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressedCiObject, CIObject thumbnailCiObject) {
        // 计算宽高
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 设置压缩后的原图地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        // 设置缩略图地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        // 返回可访问的地址
        return uploadPictureResult;
    }

    /**
     * 封装返回结果
     *
     * @param originalFilename
     * @param file
     * @param uploadPath
     * @param imageInfo        对象存储返回的图片信息
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, File file, String uploadPath, ImageInfo imageInfo) {
        // 计算宽高
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        // 封装返回结果
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        // 返回可访问的地址
        return uploadPictureResult;
    }

}
