package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class InvalidMethodMarkingStoreConfigurationException extends IllegalArgumentException {
    public InvalidMethodMarkingStoreConfigurationException(@NotNull String methodName, @NotNull String className, @NotNull Throwable cause) {
        super(getMessage(methodName, className), cause);
    }

    private static @NotNull String getMessage(@NotNull String methodName, @NotNull String className) {
        return "The method " + methodName + "::" + className + "() threw an exception. Marking store is misconfigured maybe.";
    }
}
