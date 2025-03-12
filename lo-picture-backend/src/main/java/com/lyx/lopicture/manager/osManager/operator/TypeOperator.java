package com.lyx.lopicture.manager.osManager.operator;

import org.springframework.web.multipart.MultipartFile;

@FunctionalInterface
public interface TypeOperator<R> {

    R operate(String key, MultipartFile multipartFile);

}