package com.isfett.workflow.exception;

import javax.validation.constraints.NotNull;

public class TransitionNotEnabledException extends RuntimeException {
    public TransitionNotEnabledException(@NotNull String transitionName, @NotNull String workflowName) {
        super(getMessage(transitionName, workflowName));
    }

    private static @NotNull String getMessage(@NotNull String transitionName, @NotNull String workflowName) {
        return "Transition " + transitionName + " is not enabled for workflow " + workflowName + ".";
    }
}
