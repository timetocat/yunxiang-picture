package com.lyx.lopicture.manager.osManager.operator;

@FunctionalInterface
public interface PutOperator<T, R> {

    R put(String key, T t);

}
