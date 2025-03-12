package com.lyx.lopicture.utils.field;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface FieldSupplier<T, R> extends Function<T, R>, Serializable {

}
