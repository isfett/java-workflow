package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class TransitionUndefinedException extends IllegalArgumentException {
    public TransitionUndefinedException(@NotNull String transitionName, @NotNull String workflowName) {
        super(getMessage(transitionName, workflowName));
    }

    private static @NotNull String getMessage(@NotNull String transitionName, @NotNull String workflowName) {
        return "Transition " + transitionName + " is not defined for workflow " + workflowName + ".";
    }
}
