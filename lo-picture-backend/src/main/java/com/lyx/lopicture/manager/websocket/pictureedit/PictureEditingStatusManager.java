package com.lyx.lopicture.manager.websocket.pictureedit;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片编辑状态管理器
 */
@Component
public class PictureEditingStatusManager {

    // 每张图片的编辑状态，key: pictureId, value: 当前正在编辑的用户 ID
    private final Map<Long, Long> pictureEditingUsers = new ConcurrentHashMap<>();

    public boolean isBeingEdited(Long pictureId) {
        return pictureEditingUsers.containsKey(pictureId);
    }

    public Long getEditingUser(Long pictureId) {
        return pictureEditingUsers.get(pictureId);
    }

    public void setEditingUser(Long pictureId, Long userId) {
        pictureEditingUsers.put(pictureId, userId);
    }

    public void removeEditingUser(Long pictureId) {
        pictureEditingUsers.remove(pictureId);
    }
}
