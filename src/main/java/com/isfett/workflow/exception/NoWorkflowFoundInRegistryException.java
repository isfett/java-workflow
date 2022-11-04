package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class NoWorkflowFoundInRegistryException extends RuntimeException {
    public NoWorkflowFoundInRegistryException(@NotNull String className) {
        super(getMessage(className));
    }

    private static @NotNull String getMessage(@NotNull String className) {
        return "Unable to find a workflow for class " + className + ".";
    }
}
