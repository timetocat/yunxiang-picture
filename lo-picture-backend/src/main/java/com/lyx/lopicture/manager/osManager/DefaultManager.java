package com.lyx.lopicture.manager.osManager;

import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.model.dto.file.UploadPictureResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 默认对象存储服务 (相当于空服务)
 */
@Service
@ConditionalOnProperty(name = "default.enable-os", havingValue = "false")
public class DefaultManager implements OsManager {
    /**
     * 上传对象
     *
     * @param key 唯一键
     * @param t   上传对象
     * @return 操作结果
     */
    @Override
    public <T, R> R putObject(String key, T t) {
        return (R) throwException();
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
        return (R) throwException();
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     * @return 对象字节数组
     */
    @Override
    public byte[] getObject(String key) {
        return (byte[]) throwException();
    }

    /**
     * 获取图片地址
     *
     * @param key 唯一键
     * @return 图片地址
     */
    @Override
    public String getPictureUrl(String key) {
        return (String) throwException();
    }

    /**
     * 删除对象
     *
     * @param key 唯一键
     */
    @Override
    public void deleteObject(String key) {
        throwException();
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
        return (UploadPictureResult) throwException();
    }

    private Object throwException() {
        log.error("未配置对象存储服务");
        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
    }
}
