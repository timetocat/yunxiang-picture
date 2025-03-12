package com.lyx.lopicture.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的删除请求类
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    protected Long id;

    private static final long serialVersionUID = 1L;
}