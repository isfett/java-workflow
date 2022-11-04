package com.isfett.workflow.exception;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class TooManyWorkflowsFoundInRegistryException extends RuntimeException {
    public TooManyWorkflowsFoundInRegistryException(@NotEmpty List<String> workflowNames, @NotNull String className) {
        super(getMessage(String.join(", ", workflowNames.stream().sorted().toList()), className));
    }

    private static @NotNull String getMessage(@NotNull String workflowNames, @NotNull String className) {
        return "Too many workflows (" + workflowNames + ") match this subject (" + className + "); set a different name on each and use the second (name).";
    }
}
