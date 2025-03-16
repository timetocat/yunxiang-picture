package com.lyx.lopicture.manager.osManager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import com.lyx.lopicture.config.MinioConfig;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.manager.osManager.operator.PutOperator;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import com.lyx.lopicture.utils.PictureUtils;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
@Service
@ConditionalOnBean(MinioClient.class)
@ConditionalOnProperty(name = "default.enable-os", havingValue = "true")
public class MinioManager implements OsManager {

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;

    @Value("${default.os-format:webp}")
    private String osFormat;

    // 上传对象
    private final PutOperator<MultipartFile, ObjectWriteResponse> putOperator = (key, file) -> {
        try {
            return putObject(key, file.getInputStream(), file.getSize(), file.getContentType());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    };

    // 上传对象
    private final PutOperator<File, ObjectWriteResponse> putByFileOperator = (key, file) -> {
        try (InputStream inputStream = new FileInputStream(file)) {
            return putObject(key, inputStream, file.length(), FileUtil.getMimeType(file.toPath()));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } /*finally {
            this.deleteTempFile(file);
        }*/
    };

    // 上传对象（附带图片信息）
    private final PutOperator<File, ByteArrayOutputStream> putPictureByFileOperator = (key, file) -> {
        ByteArrayOutputStream thumbnailOutputStream = null;
        ByteArrayOutputStream originOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = new FileInputStream(file);
             InputStream originInputStream = new FileInputStream(file)) {
            if (file.length() > USE_THUMBNAIL_SIZE) {
                thumbnailOutputStream = new ByteArrayOutputStream();
                processPicture(inputStream, 0.25, osFormat, thumbnailOutputStream);
                // 拼接缩略图的路径
                String thumbnailKey = getThumbnailKey(key);
                putObject(thumbnailKey, IoUtil.toStream(thumbnailOutputStream.toByteArray()),
                        thumbnailOutputStream.size(), String.format("image/%s", osFormat));
            }
            processPicture(originInputStream, 1, "webp", originOutputStream);
            putObject(getWebpKey(key), IoUtil.toStream(originOutputStream.toByteArray()),
                    originOutputStream.size(), "image/webp");
            return originOutputStream;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            IoUtil.close(thumbnailOutputStream);
            IoUtil.close(originOutputStream);
            // this.deleteTempFile(file);
        }
    };

    // 上传对象（附带图片信息）
    private final PutOperator<MultipartFile, ByteArrayOutputStream> putPictureOperator = (key, file) -> {
        ByteArrayOutputStream thumbnailOutputStream = null;
        ByteArrayOutputStream originOutputStream = new ByteArrayOutputStream();
        try {
            if (file.getSize() > USE_THUMBNAIL_SIZE) {
                thumbnailOutputStream = new ByteArrayOutputStream();
                processPicture(file.getInputStream(), 0.25, FileUtil.getSuffix(key), thumbnailOutputStream);
                // 拼接缩略图的路径
                String thumbnailKey = getThumbnailKey(key);
                putObject(thumbnailKey, IoUtil.toStream(thumbnailOutputStream.toByteArray()),
                        thumbnailOutputStream.size(), file.getContentType());
            }
            processPicture(file.getInputStream(), 1, "webp", originOutputStream);
            putObject(getWebpKey(key), IoUtil.toStream(originOutputStream.toByteArray()),
                    originOutputStream.size(), "image/webp");
            return originOutputStream;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            IoUtil.close(thumbnailOutputStream);
            IoUtil.close(originOutputStream);
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
        if (t instanceof MultipartFile) {
            return (R) putOperator.put(key, (MultipartFile) t);
        } else if (t instanceof String) {
            File file = null;
            try {
                file = processType(t, key);
                return (R) putByFileOperator.put(key, file);
            } finally {
                this.deleteTempFile(file);
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
        if (t instanceof MultipartFile) {
            return (R) putPictureOperator.put(key, processType(t, key));
        } else if (t instanceof String) {
            return (R) putPictureByFileOperator.put(key, processType(t, key));
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     * @return 对象字节数组
     */
    @Override
    public byte[] getObject(String key) {
        try {
            GetObjectArgs objectArgs = GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(key)
                    .build();
            GetObjectResponse objectResponse = minioClient.getObject(objectArgs);
            return IOUtils.toByteArray(objectResponse);
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败");
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
        validateKey(key, false);
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + key;
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    @Override
    public void deleteObject(String key) {
        try {
            String prefix = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/";
            minioClient.removeObject(RemoveObjectArgs
                    .builder()
                    .bucket(minioConfig.getBucketName())
                    .object(!key.startsWith(prefix) ? key : CharSequenceUtil.removePrefix(key, prefix))
                    .build());
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件删除失败");
        }
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
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = putPictureObject(key, inputSource);
            BufferedImage bufferedImage = ImageIO.read(IoUtil.toStream(outputStream.toByteArray()));
            int picWidth = bufferedImage.getWidth();
            int picHeight = bufferedImage.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            // 封装返回结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            String webpFilename = getWebpKey(key);
            uploadPictureResult.setUrl(getPictureUrl(webpFilename));
            String thumbnailKey = getThumbnailKey(key);
            if (validateKey(thumbnailKey, true)) {
                uploadPictureResult.setThumbnailUrl(getPictureUrl(thumbnailKey));
            }
            uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
            uploadPictureResult.setPicSize((long) outputStream.size());
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(FileUtil.extName(webpFilename));
            try {
                uploadPictureResult.setPicColor(PictureUtils.getRGB(bufferedImage));
            } catch (IOException e) {
                log.error("获取图片颜色失败！", e);
            }
            // 返回可访问的地址
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传失败！", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        } finally {
            IoUtil.close(outputStream);
        }
    }

    /**
     * 获取缩略图key
     *
     * @param key
     * @return
     */
    @Override
    public String getThumbnailKey(String key) {
        return getFilePrefixName(key) + "_thumbnail." + osFormat;
    }


    /**
     * 校验对象 key 是否存在
     *
     * @param key          唯一键
     * @param isNeedReturn 是否需要返回 (例如缩略图处理)
     * @return true: 存在, false: 不存在
     */
    private boolean validateKey(String key, boolean isNeedReturn) {
        try {
            minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket(minioConfig.getBucketName())
                    .object(key)
                    .build());
            return true;
        } catch (ServerException e) {
            log.error("minio 服务错误, 状态码 {}", e.statusCode());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件存储服务服务异常");
        } catch (ErrorResponseException e) {
            if (isNeedReturn) return false;
            if (e.errorResponse().code().equals("NoSuchKey")) {
                log.error("获取图片链接失败, key 不存在 {}", key, e);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            }
            log.error("获取图片链接失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片链接失败");
        } catch (Exception e) {
            if (isNeedReturn) return false;
            log.error("获取图片链接失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片链接失败");
        }
    }

    /**
     * 处理图片
     *
     * @param inputStream  图片流
     * @param scale        缩放比例
     * @param format       格式
     * @param outputStream 输出流
     * @throws IOException
     */
    private void processPicture(InputStream inputStream, double scale, String format,
                                OutputStream outputStream) throws IOException {
        Thumbnails.of(inputStream)
                .scale(scale)
                .outputFormat(format)
                .toOutputStream(outputStream);
    }

    /**
     * 存储对象
     *
     * @param key         唯一键
     * @param inputStream 输入流
     * @param objectSize  对象大小
     * @param contentType 文件类型
     * @return 响应结果
     * @throws Exception
     */
    private ObjectWriteResponse putObject(String key, InputStream inputStream,
                                          long objectSize, String contentType) throws Exception {
        return minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioConfig.getBucketName()) // 存储桶
                .object(key) // 文件名
                .stream(inputStream, objectSize, -1) // 文件内容
                .contentType(contentType) // 文件类型
                .build());
    }

    @NotNull
    private static String getWebpKey(String key) {
        return getFilePrefixName(key) + ".webp";
    }

    @NotNull
    private static String getFilePrefixName(String key) {
        return CharSequenceUtil.subBefore(key, FileUtil.getName(key), true)
                + FileUtil.mainName(key);
    }

}
