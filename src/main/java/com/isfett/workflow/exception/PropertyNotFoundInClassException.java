package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class PropertyNotFoundInClassException extends IllegalArgumentException {
    public PropertyNotFoundInClassException(@NotNull String fieldName, @NotNull String className, @NotNull Throwable cause) {
        super(getMessage(fieldName, className), cause);
    }

    private static @NotNull String getMessage(@NotNull String fieldName, @NotNull String className) {
        return "The property " + fieldName + " in " + className + "() does not exist or is not public.";
    }
}
