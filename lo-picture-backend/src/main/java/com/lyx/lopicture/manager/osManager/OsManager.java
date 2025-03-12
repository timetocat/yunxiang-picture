package com.lyx.lopicture.manager.osManager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface OsManager {

    Logger log = LoggerFactory.getLogger(OsManager.class);

    // 使用缩略图大小
    Long USE_THUMBNAIL_SIZE = 2 * 1024L;

    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param t   上传对象
     * @param <T> 上传对象类型
     * @param <R> 操作结果类型
     * @return 操作结果
     */
    <T, R> R putObject(String key, T t);

    /**
     * 上传对象（附带图片信息）
     *
     * @param key 唯一键
     * @param t   上传对象
     * @param <T> 上传对象类型
     * @param <R> 操作结果类型
     * @return 操作结果
     */
    <T, R> R putPictureObject(String key, T t);

    /**
     * 下载对象
     *
     * @param key 唯一键
     * @return 对象字节数组
     */
    byte[] getObject(String key);

    /**
     * 获取图片地址
     *
     * @param key 唯一键
     * @return 图片地址
     */
    String getPictureUrl(String key);

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    void deleteObject(String key);

    /**
     * 上传图片
     *
     * @param inputSource      上传对象
     * @param key              唯一键
     * @param originalFilename 文件名
     * @return 上传结果
     */
    UploadPictureResult uploadPicture(Object inputSource, String key, String originalFilename);

    /**
     * 处理上传类型
     *
     * @param t   上传对象
     * @param <T>
     * @param <R>
     * @return
     */
    default <T, R> R processType(T t, String key) {
        if (t instanceof String) {
            return (R) processType((String) t, key);
        }
        if (!(t instanceof MultipartFile))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        return (R) t;
    }

    /**
     * 处理上传类型
     *
     * @param url 对象url
     * @param key 唯一键
     * @return 处理后的对象
     */
    default File processType(String url, String key) {
        File file = null;
        try {
            file = File.createTempFile(key, null);
            HttpUtil.downloadFile(url, file);
            return file;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 删除临时文件
     *
     * @param file 文件
     */
    default void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

    /**
     * 获取缩略图key
     *
     * @param key
     * @return
     */
    static String getThumbnailKey(String key) {
        return FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
    }

}
