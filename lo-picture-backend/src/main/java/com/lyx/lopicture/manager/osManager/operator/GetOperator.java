package com.lyx.lopicture.manager.osManager.operator;

@FunctionalInterface
public interface GetOperator<R> {

    R get(String key);

}
