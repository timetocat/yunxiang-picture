package com.lyx.lopicture.manager.websocket.pictureedit.disruptor;

import com.lyx.lopicture.manager.websocket.disruptor.Event;
import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditRequestMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureEditEvent extends Event {

    /**
     * 消息
     */
    private PictureEditRequestMessage pictureEditRequestMessage;

    /**
     * 图片 id
     */
    private Long pictureId;
}
