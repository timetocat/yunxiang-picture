package com.lyx.lopicture.manager.websocket.pictureedit;

import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditMessageTypeEnum;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import com.lyx.lopicture.manager.websocket.pictureedit.strategy.MessageTypeStrategyConfig;
import com.lyx.lopicture.manager.websocket.pictureedit.strategy.PictureEditMessageStrategy;
import com.lyx.lopicture.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.List;

/**
 * 图片编辑消息处理策略执行器
 */
@Service
@Slf4j
public class PictureEditMessageStrategyExecutor {

    @Resource
    private List<PictureEditMessageStrategy> editMessageStrategyList;

    // 兜底策略
    @Resource
    private PictureEditMessageStrategy errorMessageStrategy;

    /**
     * 根据消息类型执行对应处理策略
     */
    public void handleMessage(String pictureEditMessageType, PictureEditRequestMessage requestMessage,
                              WebSocketSession session, User user, Long pictureId) throws Exception {
        PictureEditMessageStrategy editMessageStrategy =
                this.getPictureEditMessageStrategyByType(pictureEditMessageType);
        editMessageStrategy.handle(requestMessage, session, user, pictureId);
    }

    public PictureEditMessageStrategy getPictureEditMessageStrategyByType(String pictureEditMessageType) {
        PictureEditMessageTypeEnum messageTypeEnum = BaseValueEnum
                .getEnumByValue(PictureEditMessageTypeEnum.class, pictureEditMessageType);
        if (messageTypeEnum == null) {
            return errorMessageStrategy;
        }
        for (PictureEditMessageStrategy editMessageStrategy : editMessageStrategyList) {
            // 根据对应注解获取对应的应处理策略
            Class<? extends PictureEditMessageStrategy> editMessageStrategyClass = editMessageStrategy.getClass();
            if (editMessageStrategyClass.isAnnotationPresent(MessageTypeStrategyConfig.class)) {
                MessageTypeStrategyConfig strategyConfig = editMessageStrategyClass
                        .getAnnotation(MessageTypeStrategyConfig.class);
                if (strategyConfig.type().equals(messageTypeEnum)) {
                    return editMessageStrategy;
                }
            }
        }
        return errorMessageStrategy;
    }

}
