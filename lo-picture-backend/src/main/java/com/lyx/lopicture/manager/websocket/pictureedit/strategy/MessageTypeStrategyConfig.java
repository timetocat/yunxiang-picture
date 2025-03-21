package com.lyx.lopicture.manager.websocket.pictureedit.strategy;

import com.lyx.lopicture.manager.websocket.pictureedit.model.PictureEditMessageTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface MessageTypeStrategyConfig {

    /**
     * 消息类型
     */
    PictureEditMessageTypeEnum type();
}
