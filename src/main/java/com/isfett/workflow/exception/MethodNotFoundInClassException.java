package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class MethodNotFoundInClassException extends RuntimeException {
    public MethodNotFoundInClassException(@NotNull String methodName, @NotNull String className) {
        super(getMessage(methodName, className));
    }

    private static @NotNull String getMessage(@NotNull String methodName, @NotNull String className) {
        return "The method " + methodName + "::" + className + "() does not exist.";
    }
}
