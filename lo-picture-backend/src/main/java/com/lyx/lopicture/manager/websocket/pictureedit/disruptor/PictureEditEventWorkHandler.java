package com.lyx.lopicture.manager.websocket.pictureedit.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.lyx.lopicture.manager.websocket.pictureedit.PictureEditMessageStrategyExecutor;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 图片编辑事件处理器（消费者）
 */
@Component
public class PictureEditEventWorkHandler implements WorkHandler<PictureEditEvent> {

    @Resource
    private UserService userService;

    @Resource
    private PictureEditMessageStrategyExecutor pictureEditMessageStrategyExecutor;

    @Override
    public void onEvent(PictureEditEvent pictureEditEvent) throws Exception {
        PictureEditRequestMessage pictureEditRequestMessage = pictureEditEvent.getPictureEditRequestMessage();
        WebSocketSession session = pictureEditEvent.getSession();
        User user = pictureEditEvent.getUser();
        Long pictureId = pictureEditEvent.getPictureId();
        // 获取消息类别
        String type = pictureEditRequestMessage.type();
        // 调用图片编辑消息处理策略执行器
        pictureEditMessageStrategyExecutor.handleMessage(type, pictureEditRequestMessage,
                session, user, pictureId);
    }
}
