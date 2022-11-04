package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class InvalidPropertyMarkingStoreConfigurationException extends IllegalArgumentException {
    public InvalidPropertyMarkingStoreConfigurationException(@NotNull String fieldName, @NotNull String className, @NotNull Throwable cause) {
        super(getMessage(fieldName, className), cause);
    }

    private static @NotNull String getMessage(@NotNull String fieldName, @NotNull String className) {
        return "The property " + fieldName + " in " + className + "() threw an exception. Marking store is misconfigured maybe.";
    }
}
