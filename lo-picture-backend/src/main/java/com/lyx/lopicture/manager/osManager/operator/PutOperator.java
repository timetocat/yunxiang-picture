package com.lyx.lopicture.manager.osManager.operator;

import java.io.FileNotFoundException;

@FunctionalInterface
public interface PutOperator<T, R> {

    R put(String key, T t);

}
