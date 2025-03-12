package com.lyx.lopicture.utils.field;

import java.io.Serializable;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface FieldConsumer<T, R> extends BiConsumer<T, R>, Serializable {

}
